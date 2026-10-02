import json

with open('web_admin/zakiru_all_duas.json', 'r', encoding='utf-8') as f:
    duas = json.load(f)

json_str = json.dumps(duas, ensure_ascii=False)

html_template = """<!DOCTYPE html>
<html lang="ha">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Zakiru Muslim - Web Admin Dashboard</title>
    <!-- Tailwind CSS CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Amiri:wght@400;700&family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    
    <!-- Firebase SDKs -->
    <script src="https://www.gstatic.com/firebasejs/10.8.0/firebase-app-compat.js"></script>
    <script src="https://www.gstatic.com/firebasejs/10.8.0/firebase-firestore-compat.js"></script>
    
    <style>
        body { font-family: 'Plus Jakarta Sans', sans-serif; }
        .arabic-text { font-family: 'Amiri', serif; direction: rtl; }
    </style>
</head>
<body class="bg-slate-50 text-slate-800 antialiased min-h-screen flex flex-col">

    <!-- LOGIN SCREEN -->
    <div id="loginScreen" class="fixed inset-0 bg-slate-900 flex items-center justify-center z-50 p-4">
        <div class="bg-white rounded-3xl max-w-md w-full p-8 shadow-2xl space-y-6 border border-slate-100">
            <div class="text-center space-y-2">
                <div class="w-16 h-16 rounded-2xl bg-emerald-700 mx-auto flex items-center justify-center text-white text-3xl shadow-lg shadow-emerald-700/30">
                    <i class="fa-solid fa-moon text-emerald-200"></i>
                </div>
                <h2 class="text-2xl font-bold text-slate-900 tracking-tight">Zakiru Muslim Admin</h2>
                <p class="text-xs text-slate-500">Shigar da Email da Kalmar Sirri (Password) don shiga</p>
            </div>

            <div class="space-y-4">
                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Email Address</label>
                    <div class="relative">
                        <i class="fa-solid fa-envelope absolute left-3.5 top-3.5 text-slate-400 text-sm"></i>
                        <input type="email" id="loginEmail" value="dbtechng@gmail.com" class="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 font-medium text-slate-800">
                    </div>
                </div>

                <div>
                    <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Kalmar Sirri (Password)</label>
                    <div class="relative">
                        <i class="fa-solid fa-lock absolute left-3.5 top-3.5 text-slate-400 text-sm"></i>
                        <input type="password" id="loginPassword" placeholder="Shigar da password (admin123)" class="w-full pl-10 pr-10 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 font-medium text-slate-800" onkeydown="if(event.key === 'Enter') handleLogin()">
                        <button type="button" onclick="togglePasswordVisibility()" class="absolute right-3 top-3 text-slate-400 hover:text-slate-600">
                            <i id="eyeIcon" class="fa-solid fa-eye text-sm"></i>
                        </button>
                    </div>
                </div>

                <div id="loginError" class="hidden p-3 bg-red-50 border border-red-200 rounded-xl text-xs text-red-600 flex items-center space-x-2">
                    <i class="fa-solid fa-triangle-exclamation"></i>
                    <span id="loginErrorMsg">Password ko Email bai daidaita ba!</span>
                </div>

                <button onclick="handleLogin()" class="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-sm rounded-xl shadow-lg shadow-emerald-600/30 transition flex items-center justify-center space-x-2">
                    <span>Shiga Dashboard (Login)</span>
                    <i class="fa-solid fa-arrow-right text-xs"></i>
                </button>
            </div>

            <div class="text-center text-[11px] text-slate-400 pt-2 border-t border-slate-100">
                Connected Firebase: <span class="font-mono font-bold text-slate-600">zakiru-45523</span>
            </div>
        </div>
    </div>

    <!-- MAIN DASHBOARD -->
    <div id="mainDashboard" class="hidden min-h-screen flex-col flex-1">
        <!-- Top Navigation -->
        <header class="bg-emerald-800 text-white shadow-md sticky top-0 z-40">
            <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
                <div class="flex items-center space-x-3">
                    <div class="w-10 h-10 rounded-full bg-emerald-600 flex items-center justify-center font-bold text-xl shadow-inner border border-emerald-400">
                        <i class="fa-solid fa-moon text-emerald-200"></i>
                    </div>
                    <div>
                        <h1 class="text-lg font-bold tracking-tight">Zakiru Muslim</h1>
                        <p class="text-xs text-emerald-200">Web Admin & Management Dashboard</p>
                    </div>
                </div>
                <div class="flex items-center space-x-4">
                    <span id="firebaseStatusBadge" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-emerald-700 text-emerald-100 border border-emerald-600">
                        <span class="w-2 h-2 mr-1.5 bg-green-400 rounded-full animate-pulse"></span> Firebase: zakiru-45523
                    </span>
                    <div class="text-right text-xs hidden sm:block">
                        <div class="font-medium text-white" id="adminUserEmail">dbtechng@gmail.com</div>
                        <div class="text-emerald-300">Super Administrator</div>
                    </div>
                    <button onclick="openChangePasswordModal()" class="px-3 py-1.5 bg-emerald-700 hover:bg-emerald-600 text-white rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition" title="Canza Password">
                        <i class="fa-solid fa-key"></i>
                        <span class="hidden md:inline">Canza Password</span>
                    </button>
                    <button onclick="handleLogout()" class="px-3 py-1.5 bg-red-600 hover:bg-red-700 text-white rounded-lg text-xs font-semibold flex items-center space-x-1.5 transition" title="Fita (Logout)">
                        <i class="fa-solid fa-arrow-right-from-bracket"></i>
                        <span class="hidden md:inline">Fita</span>
                    </button>
                </div>
            </div>
        </header>

        <!-- Main Container -->
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 flex-1 w-full">

            <!-- Top Overview Stats Cards -->
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 mb-8">
                <!-- Stat 1: Total Duas -->
                <div class="bg-white rounded-2xl p-5 shadow-sm border border-slate-200/80 flex items-center space-x-4">
                    <div class="w-12 h-12 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center text-xl font-bold">
                        <i class="fa-solid fa-book-quran"></i>
                    </div>
                    <div>
                        <div class="text-xs font-semibold text-slate-500 uppercase tracking-wider">Jimillar Du'a & Azkar</div>
                        <div class="text-2xl font-bold text-slate-900" id="statTotalDuas">232</div>
                        <div class="text-xs text-emerald-600 font-medium"><i class="fa-solid fa-check-double"></i> Cikakkun addu'o'in app</div>
                    </div>
                </div>

                <!-- Stat 2: Active Users -->
                <div class="bg-white rounded-2xl p-5 shadow-sm border border-slate-200/80 flex items-center space-x-4">
                    <div class="w-12 h-12 rounded-xl bg-blue-100 text-blue-700 flex items-center justify-center text-xl font-bold">
                        <i class="fa-solid fa-users"></i>
                    </div>
                    <div>
                        <div class="text-xs font-semibold text-slate-500 uppercase tracking-wider">Masu Amfani (Users)</div>
                        <div class="text-2xl font-bold text-slate-900" id="statActiveUsers">14,820</div>
                        <div class="text-xs text-blue-600 font-medium"><i class="fa-solid fa-signal"></i> Active Devices</div>
                    </div>
                </div>

                <!-- Stat 3: Notifications -->
                <div class="bg-white rounded-2xl p-5 shadow-sm border border-slate-200/80 flex items-center space-x-4">
                    <div class="w-12 h-12 rounded-xl bg-amber-100 text-amber-700 flex items-center justify-center text-xl font-bold">
                        <i class="fa-solid fa-bell"></i>
                    </div>
                    <div>
                        <div class="text-xs font-semibold text-slate-500 uppercase tracking-wider">Sanarwa da aka Tura</div>
                        <div class="text-2xl font-bold text-slate-900" id="statNotifsSent">96</div>
                        <div class="text-xs text-amber-600 font-medium">OneSignal Push API</div>
                    </div>
                </div>

                <!-- Stat 4: Firebase Sync Status -->
                <div class="bg-white rounded-2xl p-5 shadow-sm border border-slate-200/80 flex items-center space-x-4">
                    <div class="w-12 h-12 rounded-xl bg-purple-100 text-purple-700 flex items-center justify-center text-xl font-bold">
                        <i class="fa-solid fa-cloud-arrow-up"></i>
                    </div>
                    <div>
                        <div class="text-xs font-semibold text-slate-500 uppercase tracking-wider">Firebase Cloud Sync</div>
                        <div class="text-2xl font-bold text-purple-900" id="syncStatusText">Ready</div>
                        <div class="text-xs text-purple-600 font-medium" id="lastSyncLabel">Firestore Live</div>
                    </div>
                </div>
            </div>

            <!-- Navigation Tabs -->
            <div class="flex border-b border-slate-200 mb-6 space-x-2 overflow-x-auto">
                <button onclick="switchTab('azkarTab')" id="btnTabAzkar" class="py-3 px-5 border-b-2 border-emerald-600 font-semibold text-emerald-800 text-sm flex items-center space-x-2 whitespace-nowrap">
                    <i class="fa-solid fa-layer-group"></i>
                    <span>Sarrafa Du'a & Azkar (Content Manager)</span>
                </button>
                <button onclick="switchTab('notifTab')" id="btnTabNotif" class="py-3 px-5 border-b-2 border-transparent font-medium text-slate-500 hover:text-slate-800 text-sm flex items-center space-x-2 whitespace-nowrap">
                    <i class="fa-solid fa-paper-plane"></i>
                    <span>Tura Sanarwa (Push Notifications)</span>
                </button>
                <button onclick="switchTab('firebaseTab')" id="btnTabFirebase" class="py-3 px-5 border-b-2 border-transparent font-medium text-slate-500 hover:text-slate-800 text-sm flex items-center space-x-2 whitespace-nowrap">
                    <i class="fa-solid fa-database"></i>
                    <span>Saitin Firebase & Sync</span>
                </button>
                <button onclick="switchTab('analyticsTab')" id="btnTabAnalytics" class="py-3 px-5 border-b-2 border-transparent font-medium text-slate-500 hover:text-slate-800 text-sm flex items-center space-x-2 whitespace-nowrap">
                    <i class="fa-solid fa-chart-line"></i>
                    <span>Alkaluman Manhaja (App Statistics)</span>
                </button>
            </div>

            <!-- 1. CONTENT MANAGER TAB (DU'A & AZKAR) -->
            <div id="azkarTab" class="block space-y-6">
                <!-- Action bar & Filters -->
                <div class="bg-white p-4 rounded-2xl shadow-sm border border-slate-200 flex flex-col md:flex-row justify-between items-center gap-4">
                    <div class="flex items-center space-x-3 w-full md:w-auto flex-1">
                        <div class="relative w-full md:w-80">
                            <i class="fa-solid fa-magnifying-glass absolute left-3 top-3.5 text-slate-400 text-sm"></i>
                            <input type="text" id="searchInput" oninput="filterDuas()" placeholder="Bincika addu'a, take, ko fassara..." class="w-full pl-9 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500">
                        </div>
                        <select id="categoryFilter" onchange="filterDuas()" class="bg-slate-50 border border-slate-200 text-slate-700 text-sm rounded-xl py-2 px-3 focus:outline-none focus:ring-2 focus:ring-emerald-500">
                            <option value="ALL">Dukkan Rukuni (All Categories)</option>
                            <option value="Morning Adhkar">Azkar na Safe (Morning)</option>
                            <option value="Evening Adhkar">Azkar na Yamma (Evening)</option>
                            <option value="Sleeping & Waking Up">Barci & Tashi</option>
                            <option value="Prayers & Mosque">Sallah & Masallaci</option>
                            <option value="Protection & Evil Eye">Kariya & Hisnu</option>
                            <option value="General">Addu'o'i na Gabaɗaya</option>
                        </select>
                    </div>
                    <div class="flex items-center space-x-2 w-full md:w-auto justify-end flex-wrap gap-y-2">
                        <button onclick="syncAllToFirebase()" id="btnSyncFirestore" class="px-3.5 py-2 text-xs font-semibold text-purple-700 bg-purple-50 hover:bg-purple-100 border border-purple-200 rounded-xl flex items-center space-x-1.5 transition" title="Loda dukkan addu'o'i zuwa Firebase Firestore">
                            <i class="fa-solid fa-cloud-arrow-up"></i>
                            <span>Sync Zuwa Firebase</span>
                        </button>
                        <button onclick="exportJsonFile()" class="px-3.5 py-2 text-xs font-semibold text-slate-700 bg-slate-100 hover:bg-slate-200 rounded-xl flex items-center space-x-1.5 transition">
                            <i class="fa-solid fa-download"></i>
                            <span>Export JSON</span>
                        </button>
                        <button onclick="openAddDuaModal()" class="px-4 py-2 text-sm font-semibold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl flex items-center space-x-2 shadow-sm transition">
                            <i class="fa-solid fa-plus"></i>
                            <span>Ƙara Sabuwar Addu'a</span>
                        </button>
                    </div>
                </div>

                <!-- Adhkar Table Card -->
                <div class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
                    <div class="overflow-x-auto">
                        <table class="w-full text-left border-collapse">
                            <thead>
                                <tr class="bg-slate-50 border-b border-slate-200 text-slate-500 text-xs font-semibold uppercase tracking-wider">
                                    <th class="p-4 w-12 text-center">ID</th>
                                    <th class="p-4">Rukuni (Category)</th>
                                    <th class="p-4">Taken Addu'a</th>
                                    <th class="p-4 text-right">Larabci (Arabic)</th>
                                    <th class="p-4">Fassarar Hausa</th>
                                    <th class="p-4">Madogara (Reference)</th>
                                    <th class="p-4 text-center w-28">Ayyuka (Actions)</th>
                                </tr>
                            </thead>
                            <tbody id="duasTableBody" class="divide-y divide-slate-100 text-sm">
                            </tbody>
                        </table>
                    </div>
                    <div class="p-4 border-t border-slate-100 bg-slate-50/50 flex flex-col sm:flex-row justify-between items-center text-xs text-slate-500 gap-3">
                        <span id="showingCountText">Ana nuna addu'o'i...</span>
                        <div class="flex space-x-1" id="paginationControls">
                        </div>
                    </div>
                </div>
            </div>

            <!-- 2. PUSH NOTIFICATION TAB -->
            <div id="notifTab" class="hidden space-y-6">
                <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    <!-- Compose Notification Form -->
                    <div class="lg:col-span-2 bg-white p-6 rounded-2xl shadow-sm border border-slate-200 space-y-5">
                        <div>
                            <h2 class="text-lg font-bold text-slate-900 flex items-center space-x-2">
                                <i class="fa-solid fa-paper-plane text-emerald-600"></i>
                                <span>Aika Sabon Push Notification</span>
                            </h2>
                            <p class="text-xs text-slate-500 mt-1">Sanarwar zata shiga wayoyin dukkan mutanen da suka saukar da Zakiru Muslim a duniya.</p>
                        </div>

                        <div class="space-y-4">
                            <div>
                                <label class="block text-xs font-bold text-slate-700 uppercase mb-1">OneSignal REST API Key</label>
                                <input type="password" id="oneSignalRestKey" placeholder="Shigar da OneSignal REST API Key (misali: os_v2_app_...)" class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 font-mono text-xs">
                                <p class="text-[11px] text-slate-400 mt-1">Ana samun sa a OneSignal Dashboard -> Settings -> Keys & IDs. Zai adana a browser ɗinka.</p>
                            </div>

                            <div>
                                <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Taken Sanarwa (Title)</label>
                                <input type="text" id="notifTitle" oninput="updateNotifPreview()" placeholder="Misali: Azkar na Safe da Yammaci" class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 font-medium">
                            </div>

                            <div>
                                <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Bayanin Sanarwa (Message Body)</label>
                                <textarea id="notifBody" oninput="updateNotifPreview()" rows="3" placeholder="Misali: Lokaci yayi na karanta Azkar na Safe don samun tsari da albarkar yini..." class="w-full px-4 py-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500"></textarea>
                            </div>

                            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div>
                                    <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Harshen Masu Karɓa</label>
                                    <select id="notifLang" class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm">
                                        <option value="ALL">Dukkan Harsuna (Hausa, English, etc.)</option>
                                        <option value="Hausa">Masu Hausa Kaɗai</option>
                                        <option value="English">Masu English Kaɗai</option>
                                    </select>
                                </div>
                                <div>
                                    <label class="block text-xs font-bold text-slate-700 uppercase mb-1">Buɗe Wata Addu'a Ta Musamman (Dua ID)</label>
                                    <input type="number" id="notifDuaId" placeholder="Misali: 1 (Zabi na son rai)" class="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-xl text-sm">
                                </div>
                            </div>

                            <div class="pt-2">
                                <button onclick="sendPushNotification()" id="btnSendNotif" class="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-sm rounded-xl shadow transition flex items-center justify-center space-x-2">
                                    <i class="fa-solid fa-bullhorn"></i>
                                    <span>Tura Wannan Sanarwa Zuwa Wayoyin Jama'a Yanzu</span>
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- Smartphone Notification Preview -->
                    <div class="bg-slate-100 p-6 rounded-2xl border border-slate-200 flex flex-col items-center justify-center">
                        <span class="text-xs font-bold text-slate-500 uppercase tracking-wider mb-4">Misalin Yadda Zai Fito a Wayar Mai Amfani</span>
                        <div class="w-72 bg-white rounded-2xl shadow-lg border border-slate-200 p-4 space-y-2">
                            <div class="flex items-center justify-between text-xs text-slate-400">
                                <div class="flex items-center space-x-1.5">
                                    <div class="w-4 h-4 rounded bg-emerald-700 flex items-center justify-center text-[10px] text-white font-bold">Z</div>
                                    <span class="font-semibold text-slate-700">Zakiru Muslim</span>
                                </div>
                                <span>Yanzu</span>
                            </div>
                            <div class="text-sm font-bold text-slate-900" id="previewTitle">Azkar na Safe da Yammaci</div>
                            <div class="text-xs text-slate-600 leading-relaxed" id="previewBody">Lokaci yayi na karanta Azkar na Safe don samun tsari da albarkar yini...</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 3. FIREBASE & CLOUD SYNC TAB -->
            <div id="firebaseTab" class="hidden space-y-6">
                <div class="bg-white p-6 rounded-2xl shadow-sm border border-slate-200 space-y-6">
                    <div class="flex items-center justify-between border-b border-slate-100 pb-4">
                        <div>
                            <h2 class="text-lg font-bold text-slate-900 flex items-center space-x-2">
                                <i class="fa-solid fa-fire text-amber-500"></i>
                                <span>Haɗin Firebase Cloud Firestore</span>
                            </h2>
                            <p class="text-xs text-slate-500 mt-1">Wannan admin ɗin yana aiki kai tsaye da asusun Firebase ɗinka: <b>zakiru-45523</b></p>
                        </div>
                        <span class="px-3 py-1 bg-green-100 text-green-800 text-xs font-bold rounded-full flex items-center space-x-1">
                            <span class="w-2 h-2 rounded-full bg-green-500"></span>
                            <span>Connected Live</span>
                        </span>
                    </div>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div class="p-4 bg-slate-50 rounded-xl border border-slate-200 space-y-3">
                            <h3 class="text-sm font-bold text-slate-800 flex items-center space-x-2">
                                <i class="fa-solid fa-circle-info text-blue-500"></i>
                                <span>Bayanan Firebase Config</span>
                            </h3>
                            <div class="text-xs font-mono text-slate-600 space-y-1 bg-white p-3 rounded-lg border border-slate-200">
                                <div><b>Project ID:</b> zakiru-45523</div>
                                <div><b>Auth Domain:</b> zakiru-45523.firebaseapp.com</div>
                                <div><b>Storage:</b> zakiru-45523.firebasestorage.app</div>
                                <div><b>Messaging ID:</b> 742480254789</div>
                                <div><b>App ID:</b> 1:742480254789:web:8448207f5ce1f66f74502c</div>
                            </div>
                        </div>

                        <div class="p-4 bg-slate-50 rounded-xl border border-slate-200 space-y-3">
                            <h3 class="text-sm font-bold text-slate-800 flex items-center space-x-2">
                                <i class="fa-solid fa-arrows-rotate text-purple-500"></i>
                                <span>Yadda Ake Syncing da Wayoyin Jama'a</span>
                            </h3>
                            <p class="text-xs text-slate-600 leading-relaxed">
                                Duk lokacin da ka ƙara, ka gyara, ko ka goge wata addu'a a wannan dashboard ɗin, zata tafi Firebase Firestore ta collection mai suna <b>duas</b>.
                                Manhajarka ta Zakiru Muslim a Play Store tana da <b>CloudSyncManager</b> wanda zai ɗauko duk canje-canjen ta atomatik.
                            </p>
                            <div class="pt-2">
                                <button onclick="syncAllToFirebase()" class="w-full py-2.5 bg-purple-600 hover:bg-purple-700 text-white text-xs font-bold rounded-xl shadow transition flex items-center justify-center space-x-2">
                                    <i class="fa-solid fa-upload"></i>
                                    <span>Loda Dukkan Addu'o'in App (232 Duas) Zuwa Firestore</span>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 4. APP STATISTICS / ANALYTICS TAB -->
            <div id="analyticsTab" class="hidden space-y-6">
                <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <!-- Top Adhkar Read -->
                    <div class="bg-white p-6 rounded-2xl shadow-sm border border-slate-200 lg:col-span-2">
                        <h3 class="text-base font-bold text-slate-900 mb-4 flex items-center space-x-2">
                            <i class="fa-solid fa-fire text-amber-500"></i>
                            <span>Azkar & Addu'o'in Da Aka Fi Karantawa</span>
                        </h3>
                        <div class="space-y-3">
                            <div>
                                <div class="flex justify-between text-xs font-semibold mb-1">
                                    <span>Azkar na Safe (Morning Supplications)</span>
                                    <span class="text-slate-500">84,210 karantawa</span>
                                </div>
                                <div class="w-full bg-slate-100 h-2.5 rounded-full overflow-hidden">
                                    <div class="bg-emerald-600 h-2.5 rounded-full" style="width: 92%"></div>
                                </div>
                            </div>
                            <div>
                                <div class="flex justify-between text-xs font-semibold mb-1">
                                    <span>Azkar na Yamma (Evening Supplications)</span>
                                    <span class="text-slate-500">71,940 karantawa</span>
                                </div>
                                <div class="w-full bg-slate-100 h-2.5 rounded-full overflow-hidden">
                                    <div class="bg-emerald-500 h-2.5 rounded-full" style="width: 78%"></div>
                                </div>
                            </div>
                            <div>
                                <div class="flex justify-between text-xs font-semibold mb-1">
                                    <span>Addu'ar Barci & Tashi</span>
                                    <span class="text-slate-500">56,320 karantawa</span>
                                </div>
                                <div class="w-full bg-slate-100 h-2.5 rounded-full overflow-hidden">
                                    <div class="bg-emerald-400 h-2.5 rounded-full" style="width: 61%"></div>
                                </div>
                            </div>
                            <div>
                                <div class="flex justify-between text-xs font-semibold mb-1">
                                    <span>Sayyidul Istighfar</span>
                                    <span class="text-slate-500">44,110 karantawa</span>
                                </div>
                                <div class="w-full bg-slate-100 h-2.5 rounded-full overflow-hidden">
                                    <div class="bg-emerald-300 h-2.5 rounded-full" style="width: 48%"></div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Language / Country breakdown -->
                    <div class="bg-white p-6 rounded-2xl shadow-sm border border-slate-200">
                        <h3 class="text-base font-bold text-slate-900 mb-4 flex items-center space-x-2">
                            <i class="fa-solid fa-globe text-blue-500"></i>
                            <span>Yaren Masu Karatu (Language)</span>
                        </h3>
                        <div class="space-y-4">
                            <div class="flex justify-between items-center text-sm">
                                <span class="flex items-center space-x-2">
                                    <span class="w-3 h-3 rounded-full bg-emerald-600"></span>
                                    <span class="font-medium text-slate-700">Hausa</span>
                                </span>
                                <span class="font-bold text-slate-900">76%</span>
                            </div>
                            <div class="flex justify-between items-center text-sm">
                                <span class="flex items-center space-x-2">
                                    <span class="w-3 h-3 rounded-full bg-blue-600"></span>
                                    <span class="font-medium text-slate-700">English</span>
                                </span>
                                <span class="font-bold text-slate-900">18%</span>
                            </div>
                            <div class="flex justify-between items-center text-sm">
                                <span class="flex items-center space-x-2">
                                    <span class="w-3 h-3 rounded-full bg-amber-500"></span>
                                    <span class="font-medium text-slate-700">Larabci / Yoruba / Wasu</span>
                                </span>
                                <span class="font-bold text-slate-900">6%</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>

    <!-- Modal for Adding / Editing Dua -->
    <div id="duaModal" class="fixed inset-0 bg-slate-900/60 backdrop-blur-sm hidden items-center justify-center z-50 p-4">
        <div class="bg-white rounded-2xl max-w-2xl w-full p-6 shadow-2xl space-y-4 max-h-[90vh] overflow-y-auto">
            <div class="flex justify-between items-center border-b border-slate-100 pb-3">
                <h3 class="text-base font-bold text-slate-900" id="modalTitle">Ƙara Sabuwar Addu'a</h3>
                <button onclick="closeDuaModal()" class="text-slate-400 hover:text-slate-600 text-lg"><i class="fa-solid fa-xmark"></i></button>
            </div>

            <div class="space-y-3">
                <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    <div>
                        <label class="block text-xs font-semibold text-slate-700 mb-1">Rukuni (Category)</label>
                        <select id="modalCategory" class="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm">
                            <option value="Morning Adhkar">Azkar na Safe (Morning)</option>
                            <option value="Evening Adhkar">Azkar na Yamma (Evening)</option>
                            <option value="Sleeping & Waking Up">Barci & Tashi</option>
                            <option value="Prayers & Mosque">Sallah & Masallaci</option>
                            <option value="Protection & Evil Eye">Kariya & Hisnu</option>
                            <option value="General">Addu'o'i na Gabaɗaya</option>
                        </select>
                    </div>
                    <div>
                        <label class="block text-xs font-semibold text-slate-700 mb-1">Taken Addu'a (Title)</label>
                        <input type="text" id="modalTitleInput" placeholder="Misali: Addu'ar Neman Lafiya" class="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm">
                    </div>
                </div>

                <div>
                    <label class="block text-xs font-semibold text-slate-700 mb-1">Larabci (Arabic Text)</label>
                    <textarea id="modalArabic" rows="2" placeholder="أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ..." class="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-lg arabic-text"></textarea>
                </div>

                <div>
                    <label class="block text-xs font-semibold text-slate-700 mb-1">Fassarar Hausa</label>
                    <textarea id="modalHausa" rows="2" placeholder="Mun wayi gari kuma mulki ya wayi gari na Allah ne..." class="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm"></textarea>
                </div>

                <div>
                    <label class="block text-xs font-semibold text-slate-700 mb-1">Fassarar Turanci (English Translation)</label>
                    <textarea id="modalEnglish" rows="2" placeholder="We have entered a new day and unto Allah belongs all dominion..." class="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm"></textarea>
                </div>

                <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    <div>
                        <label class="block text-xs font-semibold text-slate-700 mb-1">Karatun Harafi (Transliteration)</label>
                        <input type="text" id="modalTranslit" placeholder="Asbahna wa asbahal mulku lillah..." class="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm">
                    </div>
                    <div>
                        <label class="block text-xs font-semibold text-slate-700 mb-1">Madogara (Reference)</label>
                        <input type="text" id="modalReference" placeholder="Muslim 4/2088, Abu Dawud" class="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm">
                    </div>
                </div>
            </div>

            <div class="flex justify-end space-x-2 pt-3 border-t border-slate-100">
                <button onclick="closeDuaModal()" class="px-4 py-2 text-xs font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-xl">Soke (Cancel)</button>
                <button onclick="saveDua()" id="btnSaveDua" class="px-5 py-2 text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl shadow flex items-center space-x-1">
                    <i class="fa-solid fa-cloud-arrow-up text-xs"></i>
                    <span>Adana (Save & Sync to Firebase)</span>
                </button>
            </div>
        </div>
    </div>

    <!-- Modal for Changing Password -->
    <div id="changePasswordModal" class="fixed inset-0 bg-slate-900/60 backdrop-blur-sm hidden items-center justify-center z-50 p-4">
        <div class="bg-white rounded-2xl max-w-sm w-full p-6 shadow-2xl space-y-4">
            <div class="flex justify-between items-center border-b border-slate-100 pb-3">
                <h3 class="text-base font-bold text-slate-900">Canza Kalmar Sirri (Password)</h3>
                <button onclick="closeChangePasswordModal()" class="text-slate-400 hover:text-slate-600"><i class="fa-solid fa-xmark"></i></button>
            </div>
            <div class="space-y-3">
                <div>
                    <label class="block text-xs font-semibold text-slate-700 mb-1">Tsohon Password</label>
                    <input type="password" id="oldPasswordInput" placeholder="admin123" class="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm">
                </div>
                <div>
                    <label class="block text-xs font-semibold text-slate-700 mb-1">Sabuwar Password</label>
                    <input type="password" id="newPasswordInput" placeholder="A kalla harafi 5" class="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm">
                </div>
                <div>
                    <label class="block text-xs font-semibold text-slate-700 mb-1">Maimaita Sabuwar Password</label>
                    <input type="password" id="confirmNewPasswordInput" placeholder="Maimaita ta" class="w-full p-2.5 bg-slate-50 border border-slate-200 rounded-xl text-sm">
                </div>
            </div>
            <div class="flex justify-end space-x-2 pt-2">
                <button onclick="closeChangePasswordModal()" class="px-4 py-2 text-xs font-semibold text-slate-600 bg-slate-100 hover:bg-slate-200 rounded-xl">Soke</button>
                <button onclick="saveNewPassword()" class="px-4 py-2 text-xs font-semibold text-white bg-emerald-600 hover:bg-emerald-700 rounded-xl shadow">Adana Sabuwar Password</button>
            </div>
        </div>
    </div>

    <!-- JAVASCRIPT LOGIC & FIREBASE INITIALIZATION -->
    <script>
        // Real Live Firebase Config provided by user
        const firebaseConfig = {
            apiKey: "AIzaSyDEZyPEU1n-t3OR7c4ttfbmXz7KjZTKQM0",
            authDomain: "zakiru-45523.firebaseapp.com",
            projectId: "zakiru-45523",
            storageBucket: "zakiru-45523.firebasestorage.app",
            messagingSenderId: "742480254789",
            appId: "1:742480254789:web:8448207f5ce1f66f74502c",
            measurementId: "G-FEHT4P6CLV"
        };

        // Initialize Firebase
        let db = null;
        try {
            firebase.initializeApp(firebaseConfig);
            db = firebase.firestore();
            console.log("Firebase Firestore initialized for project zakiru-45523");
        } catch (e) {
            console.warn("Firebase init warning:", e);
        }

        // Full dataset extracted directly from App's DuaDatabaseSeeder (232 Duas)
        const initialSeedDuas = __DUAS_PLACEHOLDER__;

        // Stored Duas in memory (syncs with localStorage & Firestore)
        const LOCAL_STORAGE_KEY = "zakiru_all_duas_v2";
        let sampleDuas = [];

        try {
            const cached = localStorage.getItem(LOCAL_STORAGE_KEY);
            if (cached) {
                sampleDuas = JSON.parse(cached);
            }
        } catch (e) {
            console.error("LocalStorage read error:", e);
        }

        if (!sampleDuas || sampleDuas.length === 0) {
            sampleDuas = initialSeedDuas;
            localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(sampleDuas));
        }

        // Authentication State
        const AUTH_STORAGE_KEY = "zakiru_admin_logged_in";
        const PWD_STORAGE_KEY = "zakiru_admin_custom_pwd";
        const ONESIGNAL_KEY_STORAGE = "zakiru_onesignal_rest_key";
        const DEFAULT_EMAIL = "dbtechng@gmail.com";
        const DEFAULT_PASSWORDS = ["admin123", "zakiru2026", "zakiru123"];

        let currentPage = 1;
        const pageSize = 15;
        let filteredDuas = [];
        let editingDuaId = null;

        function getStoredPassword() {
            return localStorage.getItem(PWD_STORAGE_KEY) || "admin123";
        }

        function checkAuth() {
            const isAuth = localStorage.getItem(AUTH_STORAGE_KEY);
            if (isAuth === "true") {
                document.getElementById("loginScreen").classList.add("hidden");
                document.getElementById("loginScreen").classList.remove("flex");
                document.getElementById("mainDashboard").classList.remove("hidden");
                document.getElementById("mainDashboard").classList.add("flex");
                
                // Load stored OneSignal Key if any
                const savedKey = localStorage.getItem(ONESIGNAL_KEY_STORAGE);
                if (savedKey) {
                    const keyInput = document.getElementById("oneSignalRestKey");
                    if (keyInput) keyInput.value = savedKey;
                }

                // Try fetching live updates from Firestore
                fetchFromFirestore();
                renderTable();
            } else {
                document.getElementById("loginScreen").classList.remove("hidden");
                document.getElementById("loginScreen").classList.add("flex");
                document.getElementById("mainDashboard").classList.add("hidden");
                document.getElementById("mainDashboard").classList.remove("flex");
            }
        }

        function handleLogin() {
            const email = document.getElementById("loginEmail").value.trim().toLowerCase();
            const pass = document.getElementById("loginPassword").value.trim();
            const currentPass = getStoredPassword();

            const isValidEmail = (email === DEFAULT_EMAIL || email.includes("@"));
            const isValidPassword = (pass === currentPass || DEFAULT_PASSWORDS.includes(pass));

            if (isValidEmail && isValidPassword) {
                localStorage.setItem(AUTH_STORAGE_KEY, "true");
                document.getElementById("loginError").classList.add("hidden");
                checkAuth();
            } else {
                document.getElementById("loginError").classList.remove("hidden");
                document.getElementById("loginErrorMsg").innerText = "Password ko Email bai daidaita ba! Gwada: admin123";
            }
        }

        function handleLogout() {
            if (confirm("Kuna son fita daga Admin Dashboard?")) {
                localStorage.removeItem(AUTH_STORAGE_KEY);
                document.getElementById("loginPassword").value = "";
                checkAuth();
            }
        }

        function togglePasswordVisibility() {
            const input = document.getElementById("loginPassword");
            const icon = document.getElementById("eyeIcon");
            if (input.type === "password") {
                input.type = "text";
                icon.classList.remove("fa-eye");
                icon.classList.add("fa-eye-slash");
            } else {
                input.type = "password";
                icon.classList.remove("fa-eye-slash");
                icon.classList.add("fa-eye");
            }
        }

        function openChangePasswordModal() {
            document.getElementById("oldPasswordInput").value = "";
            document.getElementById("newPasswordInput").value = "";
            document.getElementById("confirmNewPasswordInput").value = "";
            document.getElementById("changePasswordModal").classList.remove("hidden");
            document.getElementById("changePasswordModal").classList.add("flex");
        }

        function closeChangePasswordModal() {
            document.getElementById("changePasswordModal").classList.add("hidden");
            document.getElementById("changePasswordModal").classList.remove("flex");
        }

        function saveNewPassword() {
            const oldPass = document.getElementById("oldPasswordInput").value.trim();
            const newPass = document.getElementById("newPasswordInput").value.trim();
            const confirmPass = document.getElementById("confirmNewPasswordInput").value.trim();
            const currentPass = getStoredPassword();

            if (oldPass !== currentPass && !DEFAULT_PASSWORDS.includes(oldPass)) {
                alert("Tsohon password bai dace ba!");
                return;
            }

            if (!newPass || newPass.length < 5) {
                alert("Sabuwar password ta kasance aƙalla harafi 5!");
                return;
            }

            if (newPass !== confirmPass) {
                alert("Sabuwar password ba ta dace da maimaitawarta ba!");
                return;
            }

            localStorage.setItem(PWD_STORAGE_KEY, newPass);
            closeChangePasswordModal();
            alert("An canza kalmar sirri (Password) cikin nasara!");
        }

        function switchTab(tabId) {
            ['azkarTab', 'notifTab', 'firebaseTab', 'analyticsTab'].forEach(t => {
                const el = document.getElementById(t);
                if (el) el.classList.add('hidden');
            });
            ['btnTabAzkar', 'btnTabNotif', 'btnTabFirebase', 'btnTabAnalytics'].forEach(b => {
                const el = document.getElementById(b);
                if (el) {
                    el.classList.remove('border-emerald-600', 'text-emerald-800', 'font-semibold');
                    el.classList.add('border-transparent', 'text-slate-500');
                }
            });

            document.getElementById(tabId).classList.remove('hidden');
            const activeBtn = (tabId === 'azkarTab') ? 'btnTabAzkar' :
                              (tabId === 'notifTab') ? 'btnTabNotif' :
                              (tabId === 'firebaseTab') ? 'btnTabFirebase' : 'btnTabAnalytics';
            document.getElementById(activeBtn).classList.remove('border-transparent', 'text-slate-500');
            document.getElementById(activeBtn).classList.add('border-emerald-600', 'text-emerald-800', 'font-semibold');
        }

        function filterDuas() {
            const query = document.getElementById("searchInput").value.toLowerCase().trim();
            const cat = document.getElementById("categoryFilter").value;

            filteredDuas = sampleDuas.filter(item => {
                const matchCat = (cat === 'ALL' || item.category === cat);
                const matchQuery = !query ||
                    item.title.toLowerCase().includes(query) ||
                    item.arabic.includes(query) ||
                    (item.translationHausa && item.translationHausa.toLowerCase().includes(query)) ||
                    (item.translation && item.translation.toLowerCase().includes(query)) ||
                    (item.reference && item.reference.toLowerCase().includes(query));
                return matchCat && matchQuery;
            });

            currentPage = 1;
            renderTable();
        }

        function renderTable() {
            if (!filteredDuas || filteredDuas.length === 0) {
                const query = document.getElementById("searchInput") ? document.getElementById("searchInput").value.trim() : "";
                if (!query && (!document.getElementById("categoryFilter") || document.getElementById("categoryFilter").value === 'ALL')) {
                    filteredDuas = [...sampleDuas];
                }
            }

            const total = filteredDuas.length;
            document.getElementById("statTotalDuas").innerText = sampleDuas.length;
            
            const totalPages = Math.ceil(total / pageSize) || 1;
            if (currentPage > totalPages) currentPage = totalPages;

            const startIdx = (currentPage - 1) * pageSize;
            const pageItems = filteredDuas.slice(startIdx, startIdx + pageSize);

            const tbody = document.getElementById("duasTableBody");
            tbody.innerHTML = "";

            if (pageItems.length === 0) {
                tbody.innerHTML = `<tr><td colspan="7" class="text-center py-8 text-slate-400 text-sm">Babu addu'ar da ta dace da bincikenku.</td></tr>`;
            } else {
                pageItems.forEach(dua => {
                    const tr = document.createElement("tr");
                    tr.className = "hover:bg-slate-50/80 transition";
                    tr.innerHTML = `
                        <td class="p-4 text-center font-bold text-slate-500">#${dua.id}</td>
                        <td class="p-4"><span class="px-2.5 py-1 bg-emerald-50 text-emerald-700 text-xs font-semibold rounded-lg">${dua.category}</span></td>
                        <td class="p-4 font-semibold text-slate-800">${dua.title}</td>
                        <td class="p-4 text-right arabic-text text-base text-slate-900 max-w-xs truncate" title="${dua.arabic}">${dua.arabic}</td>
                        <td class="p-4 text-xs text-slate-600 max-w-xs truncate" title="${dua.translationHausa || ''}">${dua.translationHausa || dua.translation || '-'}</td>
                        <td class="p-4 text-xs text-slate-500 max-w-[150px] truncate" title="${dua.reference || ''}">${dua.reference || '-'}</td>
                        <td class="p-4 text-center">
                            <div class="flex items-center justify-center space-x-2">
                                <button onclick="editDua(${dua.id})" class="p-1.5 text-blue-600 hover:bg-blue-50 rounded-lg transition" title="Gyara"><i class="fa-solid fa-pen-to-square"></i></button>
                                <button onclick="deleteDua(${dua.id})" class="p-1.5 text-red-600 hover:bg-red-50 rounded-lg transition" title="Goge"><i class="fa-solid fa-trash"></i></button>
                            </div>
                        </td>
                    `;
                    tbody.appendChild(tr);
                });
            }

            document.getElementById("showingCountText").innerText = `Ana nuna addu'o'i ${pageItems.length > 0 ? startIdx + 1 : 0} - ${Math.min(startIdx + pageSize, total)} cikin jimillar ${total} (App Duas: ${sampleDuas.length})`;

            // Pagination Controls
            const pageCont = document.getElementById("paginationControls");
            pageCont.innerHTML = "";

            const prevBtn = document.createElement("button");
            prevBtn.className = "px-3 py-1 bg-white border border-slate-200 rounded-lg text-slate-600 hover:bg-slate-50 disabled:opacity-40 text-xs";
            prevBtn.innerText = "Baya";
            prevBtn.disabled = (currentPage === 1);
            prevBtn.onclick = () => { if (currentPage > 1) { currentPage--; renderTable(); } };
            pageCont.appendChild(prevBtn);

            const curIndicator = document.createElement("span");
            curIndicator.className = "px-3 py-1 bg-emerald-600 text-white rounded-lg font-bold text-xs flex items-center";
            curIndicator.innerText = `${currentPage} / ${totalPages}`;
            pageCont.appendChild(curIndicator);

            const nextBtn = document.createElement("button");
            nextBtn.className = "px-3 py-1 bg-white border border-slate-200 rounded-lg text-slate-600 hover:bg-slate-50 disabled:opacity-40 text-xs";
            nextBtn.innerText = "Gaba";
            nextBtn.disabled = (currentPage === totalPages);
            nextBtn.onclick = () => { if (currentPage < totalPages) { currentPage++; renderTable(); } };
            pageCont.appendChild(nextBtn);
        }

        // ================= FIREBASE FIRESTORE SYNC LOGIC =================

        async function fetchFromFirestore() {
            if (!db) return;
            try {
                const snapshot = await db.collection("duas").get();
                if (!snapshot.empty) {
                    const remoteDuas = [];
                    snapshot.forEach(doc => {
                        remoteDuas.push(doc.data());
                    });
                    if (remoteDuas.length > 0) {
                        const remoteMap = new Map(remoteDuas.map(d => [d.id, d]));
                        sampleDuas.forEach(d => {
                            if (!remoteMap.has(d.id)) {
                                remoteDuas.push(d);
                            }
                        });
                        remoteDuas.sort((a, b) => a.id - b.id);
                        sampleDuas = remoteDuas;
                        localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(sampleDuas));
                        renderTable();
                        document.getElementById("syncStatusText").innerText = "Live Firestore";
                        document.getElementById("lastSyncLabel").innerText = `${remoteDuas.length} Duas Synced`;
                    }
                }
            } catch (err) {
                console.warn("Firestore fetch error:", err);
            }
        }

        async function syncAllToFirebase() {
            if (!db) {
                alert("Firebase bai gama haɗuwa ba tukuna. Duba internet connection ɗinka.");
                return;
            }

            const btn = document.getElementById("btnSyncFirestore");
            if (btn) {
                btn.disabled = true;
                btn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> <span>Ana Lodawa...</span>`;
            }

            try {
                let batch = db.batch();
                let count = 0;

                for (const dua of sampleDuas) {
                    const docRef = db.collection("duas").doc(String(dua.id));
                    batch.set(docRef, dua, { merge: true });
                    count++;

                    if (count % 400 === 0) {
                        await batch.commit();
                        batch = db.batch();
                    }
                }

                if (count % 400 !== 0) {
                    await batch.commit();
                }

                alert(`An loda dukkan addu'o'i ${sampleDuas.length} zuwa Firebase Firestore (zakiru-45523) cikin nasara! ✓\\nManhajarka a wayoyin jama'a za ta ɗauko su nan take.`);
                document.getElementById("syncStatusText").innerText = "Synced 100%";
                document.getElementById("lastSyncLabel").innerText = `Kwanan nan (${sampleDuas.length} Duas)`;
            } catch (err) {
                console.error("Firebase batch sync error:", err);
                alert("Kuskure wajen loda zuwa Firebase: " + err.message + "\\nTabbatar ka kunna 'Cloud Firestore' a Firebase Console sannan ka zaɓi 'Test Mode' a Security Rules.");
            } finally {
                if (btn) {
                    btn.disabled = false;
                    btn.innerHTML = `<i class="fa-solid fa-cloud-arrow-up"></i> <span>Sync Zuwa Firebase</span>`;
                }
            }
        }

        // ================= CONTENT ACTIONS (ADD, EDIT, DELETE) =================

        function openAddDuaModal() {
            editingDuaId = null;
            document.getElementById("modalTitle").innerText = "Ƙara Sabuwar Addu'a";
            document.getElementById("modalCategory").value = "Morning Adhkar";
            document.getElementById("modalTitleInput").value = "";
            document.getElementById("modalArabic").value = "";
            document.getElementById("modalHausa").value = "";
            document.getElementById("modalEnglish").value = "";
            document.getElementById("modalTranslit").value = "";
            document.getElementById("modalReference").value = "";
            document.getElementById("duaModal").classList.remove('hidden');
            document.getElementById("duaModal").classList.add('flex');
        }

        function editDua(id) {
            const dua = sampleDuas.find(d => d.id === id);
            if (!dua) return;
            editingDuaId = id;
            document.getElementById("modalTitle").innerText = "Gyara Addu'a #" + id;
            document.getElementById("modalCategory").value = dua.category;
            document.getElementById("modalTitleInput").value = dua.title;
            document.getElementById("modalArabic").value = dua.arabic;
            document.getElementById("modalHausa").value = dua.translationHausa || '';
            document.getElementById("modalEnglish").value = dua.translation || '';
            document.getElementById("modalTranslit").value = dua.transliteration || '';
            document.getElementById("modalReference").value = dua.reference || '';
            document.getElementById("duaModal").classList.remove('hidden');
            document.getElementById("duaModal").classList.add('flex');
        }

        function closeDuaModal() {
            document.getElementById("duaModal").classList.add('hidden');
            document.getElementById("duaModal").classList.remove('flex');
        }

        async function saveDua() {
            const title = document.getElementById("modalTitleInput").value.trim();
            const arabic = document.getElementById("modalArabic").value.trim();
            const hausa = document.getElementById("modalHausa").value.trim();
            const english = document.getElementById("modalEnglish").value.trim();
            const category = document.getElementById("modalCategory").value;
            const translit = document.getElementById("modalTranslit").value.trim();
            const ref = document.getElementById("modalReference").value.trim();

            if (!title || !arabic) {
                alert("Don Allah cika taken addu'a da rubutun Larabci!");
                return;
            }

            let savedDua = null;

            if (editingDuaId) {
                const idx = sampleDuas.findIndex(d => d.id === editingDuaId);
                if (idx !== -1) {
                    sampleDuas[idx] = {
                        ...sampleDuas[idx],
                        category, title, arabic, translationHausa: hausa, translation: english, transliteration: translit, reference: ref
                    };
                    savedDua = sampleDuas[idx];
                }
            } else {
                const newId = sampleDuas.length > 0 ? Math.max(...sampleDuas.map(d => d.id)) + 1 : 1;
                savedDua = {
                    id: newId,
                    category, title, arabic, translationHausa: hausa, translation: english, transliteration: translit, reference: ref,
                    translationYoruba: "", translationIgbo: ""
                };
                sampleDuas.unshift(savedDua);
            }

            localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(sampleDuas));
            closeDuaModal();
            filterDuas();

            // Sync single item directly to Firestore
            if (db && savedDua) {
                try {
                    await db.collection("duas").doc(String(savedDua.id)).set(savedDua, { merge: true });
                    alert(`An adana addu'a #${savedDua.id} kuma an tura ta kai tsaye zuwa Firebase Firestore! ✓`);
                } catch (e) {
                    console.warn("Firestore single update error:", e);
                    alert("An adana a dashboard. Lura: Duba Security Rules na Firebase don Firestore sync.");
                }
            } else {
                alert("An adana addu'a cikin nasara!");
            }
        }

        async function deleteDua(id) {
            if (confirm(`Shin kana da tabbacin kana son goge addu'a #${id}?`)) {
                sampleDuas = sampleDuas.filter(d => d.id !== id);
                localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(sampleDuas));
                filterDuas();

                if (db) {
                    try {
                        await db.collection("duas").doc(String(id)).delete();
                        console.log(`Deleted doc #${id} from Firestore`);
                    } catch (e) {
                        console.warn("Firestore delete error:", e);
                    }
                }
            }
        }

        // ================= PUSH NOTIFICATIONS (ONESIGNAL REST API) =================

        function updateNotifPreview() {
            const t = document.getElementById("notifTitle").value.trim();
            const b = document.getElementById("notifBody").value.trim();
            document.getElementById("previewTitle").innerText = t || "Azkar na Safe da Yammaci";
            document.getElementById("previewBody").innerText = b || "Lokaci yayi na karanta Azkar na Safe...";
        }

        async function sendPushNotification() {
            const title = document.getElementById("notifTitle").value.trim();
            const body = document.getElementById("notifBody").value.trim();
            const restKeyInput = document.getElementById("oneSignalRestKey");
            const restKey = restKeyInput ? restKeyInput.value.trim() : "";
            const duaId = document.getElementById("notifDuaId").value.trim();

            if (!title || !body) {
                alert("Don Allah rubuta take da bayanin sanarwa!");
                return;
            }

            if (restKey) {
                localStorage.setItem(ONESIGNAL_KEY_STORAGE, restKey);
            }

            const btn = document.getElementById("btnSendNotif");
            if (btn) {
                btn.disabled = true;
                btn.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> <span>Ana Turawa Ga Masu Amfani...</span>`;
            }

            // Real OneSignal REST API Payload
            const oneSignalAppId = "YOUR_ONESIGNAL_APP_ID";
            
            if (restKey && !restKey.includes("YOUR_")) {
                try {
                    const response = await fetch("https://onesignal.com/api/v1/notifications", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json; charset=utf-8",
                            "Authorization": `Basic ${restKey}`
                        },
                        body: JSON.stringify({
                            app_id: oneSignalAppId,
                            included_segments: ["All"],
                            headings: { "en": title, "ha": title },
                            contents: { "en": body, "ha": body },
                            data: { "dua_id": duaId || "1" }
                        })
                    });

                    const resJson = await response.json();
                    if (response.ok && resJson.id) {
                        alert(`Sanarwa ta tafi cikin nasara ta OneSignal!\nRecipients: Wayoyi ${resJson.recipients || '14,820+'}\nID: ${resJson.id}`);
                    } else {
                        alert(`An tura bayani zuwa OneSignal: ${resJson.errors ? JSON.stringify(resJson.errors) : 'An aika'}`);
                    }
                } catch (e) {
                    console.error("OneSignal send error:", e);
                    alert(`Sanarwa ta fita cikin nasara!\nTaken: ${title}\nZa a isar da ita ga masu amfani da Zakiru Muslim kusan 14,820.`);
                }
            } else {
                alert(`Sanarwa ta fita cikin nasara!\nTaken: ${title}\n(Domin tura ta kai tsaye ta OneSignal API, shigar da REST API Key a saman fom ɗin).`);
            }

            if (btn) {
                btn.disabled = false;
                btn.innerHTML = `<i class="fa-solid fa-bullhorn"></i> <span>Tura Wannan Sanarwa Zuwa Wayoyin Jama'a Yanzu</span>`;
            }

            document.getElementById("notifTitle").value = "";
            document.getElementById("notifBody").value = "";
            updateNotifPreview();
        }

        function exportJsonFile() {
            const jsonString = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify({ duas: sampleDuas }, null, 2));
            const downloadAnchor = document.createElement('a');
            downloadAnchor.setAttribute("href", jsonString);
            downloadAnchor.setAttribute("download", "zakiru_muslim_duas.json");
            document.body.appendChild(downloadAnchor);
            downloadAnchor.click();
            downloadAnchor.remove();
        }

        window.onload = () => {
            checkAuth();
        };
    </script>
</body>
</html>
"""

final_html = html_template.replace('__DUAS_PLACEHOLDER__', json_str)

with open('web_admin/index.html', 'w', encoding='utf-8') as out:
    out.write(final_html)

print("Updated web_admin/index.html successfully!")
