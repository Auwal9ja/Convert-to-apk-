package com.example.util

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface UpdateState {
    object Idle : UpdateState
    object Checking : UpdateState
    data class UpdateAvailable(
        val appUpdateInfo: AppUpdateInfo,
        val availableVersionCode: Int,
        val clientVersionStalenessDays: Int?
    ) : UpdateState
    data class Downloading(
        val bytesDownloaded: Long,
        val totalBytesToDownload: Long,
        val percent: Int
    ) : UpdateState
    data class Downloaded(val appUpdateInfo: AppUpdateInfo) : UpdateState
    object UpToDate : UpdateState
    data class Error(val message: String) : UpdateState
}

/**
 * Manages Google Play In-App Updates (Flexible and Immediate) cleanly across the lifecycle.
 */
class InAppUpdateManager(private val context: Context) {

    companion object {
        private const val TAG = "NoorZikir_InAppUpdate"
        const val UPDATE_REQUEST_CODE = 9001
    }

    private val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(context)
    }

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADING -> {
                val bytes = state.bytesDownloaded()
                val total = state.totalBytesToDownload()
                val percent = if (total > 0) ((bytes * 100) / total).toInt() else 0
                Log.d(TAG, "In-App update downloading: $bytes / $total ($percent%)")
                _updateState.value = UpdateState.Downloading(bytes, total, percent)
            }
            InstallStatus.DOWNLOADED -> {
                Log.d(TAG, "In-App update downloaded and ready to install!")
                appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                    _updateState.value = UpdateState.Downloaded(info)
                }.addOnFailureListener {
                    _updateState.value = UpdateState.Downloaded(
                        // If info fetch fails, still maintain Downloaded state
                        _updateState.value.let { s ->
                            if (s is UpdateState.UpdateAvailable) s.appUpdateInfo else null
                        } ?: return@addOnFailureListener
                    )
                }
            }
            InstallStatus.FAILED -> {
                Log.e(TAG, "In-App update installation failed with errorCode: ${state.installErrorCode()}")
                _updateState.value = UpdateState.Error("Installation failed (Code: ${state.installErrorCode()})")
            }
            InstallStatus.CANCELED -> {
                Log.d(TAG, "In-App update cancelled by user")
                _updateState.value = UpdateState.Idle
            }
            InstallStatus.INSTALLED -> {
                Log.d(TAG, "In-App update installed successfully")
                _updateState.value = UpdateState.UpToDate
                unregisterListener()
            }
            else -> {
                Log.d(TAG, "In-App update installStatus: ${state.installStatus()}")
            }
        }
    }

    private var isListenerRegistered = false

    fun registerListener() {
        if (!isListenerRegistered) {
            try {
                appUpdateManager.registerListener(installStateUpdatedListener)
                isListenerRegistered = true
            } catch (e: Exception) {
                Log.w(TAG, "Could not register InstallStateUpdatedListener: ${e.message}")
            }
        }
    }

    fun unregisterListener() {
        if (isListenerRegistered) {
            try {
                appUpdateManager.unregisterListener(installStateUpdatedListener)
                isListenerRegistered = false
            } catch (e: Exception) {
                Log.w(TAG, "Could not unregister InstallStateUpdatedListener: ${e.message}")
            }
        }
    }

    /**
     * Checks for updates from Google Play Store.
     * @param isManual True if initiated by the user tapping "Check for updates" in Settings.
     */
    fun checkForUpdates(isManual: Boolean = false, onResult: ((Boolean, String) -> Unit)? = null) {
        registerListener()
        _updateState.value = UpdateState.Checking

        try {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                val availability = appUpdateInfo.updateAvailability()
                val installStatus = appUpdateInfo.installStatus()
                val availableVersion = appUpdateInfo.availableVersionCode()
                val stalenessDays = appUpdateInfo.clientVersionStalenessDays()

                Log.d(TAG, "AppUpdateInfo: availability=$availability, installStatus=$installStatus, availableVersionCode=$availableVersion")

                if (installStatus == InstallStatus.DOWNLOADED) {
                    _updateState.value = UpdateState.Downloaded(appUpdateInfo)
                    onResult?.invoke(true, "Downloaded & ready to install")
                } else if (availability == UpdateAvailability.UPDATE_AVAILABLE) {
                    _updateState.value = UpdateState.UpdateAvailable(
                        appUpdateInfo = appUpdateInfo,
                        availableVersionCode = availableVersion,
                        clientVersionStalenessDays = stalenessDays
                    )
                    onResult?.invoke(true, "Update available: Build $availableVersion")
                } else if (availability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // Resume active update
                    _updateState.value = UpdateState.Downloading(0, 0, 0)
                    onResult?.invoke(true, "Update in progress")
                } else {
                    _updateState.value = UpdateState.UpToDate
                    onResult?.invoke(false, "App is up to date")
                }
            }.addOnFailureListener { error ->
                Log.w(TAG, "Check for update failed: ${error.message}")
                _updateState.value = if (isManual) UpdateState.Error(error.localizedMessage ?: "Could not reach Play Store") else UpdateState.Idle
                onResult?.invoke(false, error.localizedMessage ?: "Error checking updates")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during check for updates: ${e.message}")
            _updateState.value = if (isManual) UpdateState.Error(e.localizedMessage ?: "Error") else UpdateState.Idle
            onResult?.invoke(false, e.localizedMessage ?: "Error")
        }
    }

    /**
     * Resumes updates when returning to the app in onResume().
     */
    fun onResume(activity: Activity) {
        try {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    _updateState.value = UpdateState.Downloaded(appUpdateInfo)
                } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an immediate update was triggered and is still in progress, re-launch it
                    startImmediateUpdate(activity, appUpdateInfo)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "onResume update check failed: ${e.message}")
        }
    }

    /**
     * Starts Flexible in-app update flow using the modern ActivityResultLauncher or fallback.
     */
    fun startFlexibleUpdate(
        activity: Activity,
        appUpdateInfo: AppUpdateInfo,
        launcher: ActivityResultLauncher<IntentSenderRequest>? = null
    ): Boolean {
        registerListener()
        return try {
            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                if (launcher != null) {
                    val starter = com.google.android.play.core.common.IntentSenderForResultStarter { intentSender, _, fillInIntent, flagsMask, flagsValues, _, _ ->
                        val request = IntentSenderRequest.Builder(intentSender)
                            .setFillInIntent(fillInIntent)
                            .setFlags(flagsValues, flagsMask)
                            .build()
                        launcher.launch(request)
                    }
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        starter,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                        UPDATE_REQUEST_CODE
                    )
                } else {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        activity,
                        UPDATE_REQUEST_CODE
                    )
                }
                true
            } else {
                Log.w(TAG, "Flexible update not allowed for this update")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting flexible update: ${e.message}", e)
            false
        }
    }

    /**
     * Starts Immediate in-app update flow (for mandatory updates).
     */
    fun startImmediateUpdate(
        activity: Activity,
        appUpdateInfo: AppUpdateInfo,
        launcher: ActivityResultLauncher<IntentSenderRequest>? = null
    ): Boolean {
        return try {
            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                if (launcher != null) {
                    val starter = com.google.android.play.core.common.IntentSenderForResultStarter { intentSender, _, fillInIntent, flagsMask, flagsValues, _, _ ->
                        val request = IntentSenderRequest.Builder(intentSender)
                            .setFillInIntent(fillInIntent)
                            .setFlags(flagsValues, flagsMask)
                            .build()
                        launcher.launch(request)
                    }
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        starter,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
                        UPDATE_REQUEST_CODE
                    )
                } else {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        activity,
                        UPDATE_REQUEST_CODE
                    )
                }
                true
            } else {
                Log.w(TAG, "Immediate update not allowed for this update")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting immediate update: ${e.message}", e)
            false
        }
    }

    /**
     * Completes flexible update by restarting the application.
     */
    fun completeUpdate() {
        try {
            appUpdateManager.completeUpdate()
        } catch (e: Exception) {
            Log.e(TAG, "Error completing update: ${e.message}", e)
        }
    }
}
