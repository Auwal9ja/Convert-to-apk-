# Jagoran Web Admin Dashboard na Zakiru Muslim

Wannan folda tana ɗauke da cikakken tsarin **Web Admin Dashboard** na manhajar **Zakiru Muslim**.

---

## 1. Abubuwan da Shafin Web Admin Yake Yi:

1. **Sarrafa Du'a & Azkar (Content Manager):**
   - Ƙara sabuwar addu'a a kowane lokaci (Taken addu'a, Larabci, Fassarar Hausa, Turanci, Transliteration, da Madogara).
   - Gyara ko goge kowace addu'a.
   - Bincike da tacewa gwargwadon rukuni (Misali: *Azkar na Safe, Yamma, Sallah, Kariya, da sauransu*).
   - Fitar da bayanan zuwa JSON (`Export JSON`).

2. **Tura Push Notifications:**
   - Rubuta take da bayanin sanarwa.
   - Zaɓi harshen da kake son sanarwar ta je wa (Duka, Hausa kawai, ko English).
   - Zaɓar wata addu'a ta musamman (Dua ID) wadda za ta buɗe kai tsaye idan mai amfani ya danna sanarwar a wayarsa.
   - Ganin misalin yadda sanarwar za ta fito a wayar mai amfani kafin a tura ta (*Live Smartphone Preview*).

3. **Ganin Alkaluman Manhaja (App Statistics & Insights):**
   - Adadin masu amfani da manhaja a duniya.
   - Azkar da addu'o'in da aka fi karantawa.
   - Rarrabuwar yaren masu karatu.

---

## 2. Bayanin Shiga Shafin (Login & Password):

Don tsaron shafin daga kowa shiga, an saka masa tsarin tantancewa:
- **Email:** `dbtechng@gmail.com`
- **Default Password:** `admin123` (ko `zakiru2026`)
- Da zarar ka shiga, za ka ga madannin **"Canza Password"** a saman shafin domin saita password ɗin da kake so.

---

## 3. Idan Netlify Da Kanta Ta Tambayi Password ("Site Protection / Access Control"):
Idan a shafin Netlify ka ga akwai tambayar password kafin shafin ya buɗe:
1. Shiga shafinka na **Netlify Dashboard** -> danna sunan shafinka (site).
2. Tafi sashen **Site configuration** -> **Access management** -> **Visitor access**.
3. Za ka ga **Site protection / Password protection**. Idan yana kan *Enabled*, danna **Disable** ko ka goge password ɗin da aka saita a wurin don kowa ya iya buɗe shafin, ko kuma ka sanya password ɗin da ka sani.

---

## 4. Yadda Ake Buɗe Shafin Web Admin:

- Shafin yana cikin fayil ɗin `/web_admin/index.html`.
- Za ka iya danna shi sau biyu a kwamfutarka don buɗe shi a Google Chrome, Firefox, Safari, ko kowace browser.
- Yana da tsari mai kyau (Responsive Tailwind CSS) wanda ke buɗewa lafiya a kwamfuta, kwamfutar tafi-da-gidanka (Laptop), ko a wayar hannu.

---

## 3. Yadda Ake Ɗora Shi a Yanar Gizo (Online Hosting - Kyauta):

Idan kana son kowa a tawagarka ya iya shiga ta intanet ta kowace na'ura:

1. **Vercel ko Netlify:**
   - Kawai jawo foldar `web_admin` ka sanya a [Netlify Drop](https://app.netlify.com/drop) ko [Vercel](https://vercel.com).
   - Zai baka adireshin yanar gizo nan take (Misali: `https://zakiru-admin.netlify.app`).

2. **Firebase Hosting / Cloudflare Pages:**
   - Za a iya haɗa shi da Firebase project ɗinka don amfani da Cloud Firestore da OneSignal REST API.
