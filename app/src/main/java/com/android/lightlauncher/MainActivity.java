/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.app.ActivityManager
 *  android.app.ActivityManager$MemoryInfo
 *  android.app.AlertDialog$Builder
 *  android.content.Context
 *  android.content.Intent
 *  android.content.pm.ApplicationInfo
 *  android.content.pm.ResolveInfo
 *  android.graphics.Bitmap
 *  android.graphics.Bitmap$Config
 *  android.graphics.BitmapFactory
 *  android.graphics.BitmapFactory$Options
 *  android.graphics.Canvas
 *  android.graphics.Paint
 *  android.graphics.Paint$Cap
 *  android.graphics.Paint$Style
 *  android.graphics.Typeface
 *  android.graphics.drawable.Drawable
 *  android.graphics.drawable.GradientDrawable
 *  android.net.ConnectivityManager
 *  android.net.Network
 *  android.net.NetworkCapabilities
 *  android.net.NetworkInfo
 *  android.net.Uri
 *  android.os.Build$VERSION
 *  android.os.Bundle
 *  android.os.Handler
 *  android.renderscript.Allocation
 *  android.renderscript.Element
 *  android.renderscript.RenderScript
 *  android.renderscript.ScriptIntrinsicBlur
 *  android.view.KeyEvent
 *  android.view.View
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.widget.AbsListView$LayoutParams
 *  android.widget.BaseAdapter
 *  android.widget.Button
 *  android.widget.FrameLayout
 *  android.widget.FrameLayout$LayoutParams
 *  android.widget.GridView
 *  android.widget.HorizontalScrollView
 *  android.widget.ImageView
 *  android.widget.ImageView$ScaleType
 *  android.widget.LinearLayout
 *  android.widget.LinearLayout$LayoutParams
 *  android.widget.ListAdapter
 *  android.widget.ScrollView
 *  android.widget.TextView
 *  android.widget.Toast
 */
package com.android.lightlauncher;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity
extends Activity {
    private static final int PAGE_BG = -15394270;
    private static final int PANEL = -2144849605;
    private static final int PANEL_FOCUS = -1724099998;
    private static final int TEXT = -657415;
    private static final int MUTED = -4142640;
    private static final int ACCENT = -8857652;
    private static final int GREEN = -9248846;
    private static final int YELLOW = -11930;
    private static final int RED = -30080;
    private static final int PICK_WALLPAPER = 202;
    private final Handler handler = new Handler();
    private final List<AppEntry> apps = new ArrayList<AppEntry>();
    private LinearLayout root;
    private LinearLayout content;
    private LinearLayout tabs;
    private GridView appGrid;
    private FrameLayout scene;
    private ImageView wallpaper;
    private TextView clock;
    private TextView dateLine;
    private TextView selectionHint;
    private TextView ramPercent;
    private StatusIconView wifiIcon;
    private AppEntry selected;
    private String page = "apps";
    private boolean ukrainian = true;
    private boolean arabic = false;
    private String languageCode = "uk";
    private String wallpaperReturnPage = "settings";
    private String faqReturnPage = "settings";
    private int setupStep = 0;
    private static final String[] LANG_CODES = {"en", "uk", "es", "fr", "de", "pl", "it", "tr", "ar"};
    private static final String[] LANG_NAMES = {"English", "Українська", "Español", "Français", "Deutsch", "Polski", "Italiano", "Türkçe", "العربية"};
    private boolean animations = true;
    private int uiScale = 100;
    private final Runnable clockTicker = new Runnable(){

        @Override
        public void run() {
            if (MainActivity.this.clock != null) {
                MainActivity.this.clock.setText((CharSequence)new SimpleDateFormat("HH:mm", Locale.ROOT).format(System.currentTimeMillis()));
            }
            if (MainActivity.this.dateLine != null) {
                Locale locale = MainActivity.this.localeForLanguage();
                MainActivity.this.clock.setText(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT, locale).format(new Date()));
                MainActivity.this.dateLine.setText(java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL, locale).format(new Date()));
            }
            MainActivity.this.updateConnectionIcons();
            MainActivity.this.updateRamDisplay();
            MainActivity.this.handler.postDelayed((Runnable)this, 3000L);
        }
    };

    public void onCreate(Bundle state) {
        super.onCreate(state);
        this.getWindow().setFlags(1024, 1024);
        this.languageCode = this.getPreferences(0).getString("language", this.getPreferences(0).getBoolean("uk", true) ? "uk" : "en");
        this.ukrainian = "uk".equals(this.languageCode);
        this.arabic = "ar".equals(this.languageCode);
        this.animations = this.getPreferences(0).getBoolean("animations", true);
        this.uiScale = this.getPreferences(0).getInt("ui_scale", 100);
        this.loadApps();
        this.buildShell();
        if (this.getPreferences(0).getBoolean("setup_complete", false)) this.showPage("pinned");
        else this.showPage("setup");
        this.handler.post(this.clockTicker);
    }

    private String s(String ua, String en) {
        if ("ar".equals(this.languageCode)) return this.arabicText(en);
        if (!"uk".equals(this.languageCode)) {
            if (!"en".equals(this.languageCode)) return this.localized(en);
            return en;
        }
        switch (en) {
            case "Home": {
                return "\u0413\u043e\u043b\u043e\u0432\u043d\u0430";
            }
            case "All apps": {
                return "\u0423\u0441\u0456 \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u0438";
            }
            case "Pinned apps": {
                return "\u0417\u0430\u043a\u0440\u0456\u043f\u043b\u0435\u043d\u0456 \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u0438";
            }
            case "Settings": {
                return "\u041d\u0430\u043b\u0430\u0448\u0442\u0443\u0432\u0430\u043d\u043d\u044f";
            }
            case "Set as default launcher": {
                return "\u0417\u0440\u043e\u0431\u0438\u0442\u0438 \u043b\u0430\u0443\u043d\u0447\u0435\u0440 \u0437\u0430 \u0437\u0430\u043c\u043e\u0432\u0447\u0443\u0432\u0430\u043d\u043d\u044f\u043c";
            }
            case "Add app": {
                return "\u0414\u043e\u0434\u0430\u0442\u0438 \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u0443";
            }
            case "Choose an app to pin": {
                return "\u0412\u0438\u0431\u0435\u0440\u0456\u0442\u044c \u043f\u0440\u043e\u0433\u0440\u0430\u043c\u0443 \u0434\u043b\u044f \u0437\u0430\u043a\u0440\u0456\u043f\u043b\u0435\u043d\u043d\u044f";
            }
            case "No pinned apps": {
                return "\u0417\u0430\u043a\u0440\u0456\u043f\u043b\u0435\u043d\u0438\u0445 \u043f\u0440\u043e\u0433\u0440\u0430\u043c \u0449\u0435 \u043d\u0435\u043c\u0430\u0454";
            }
            case "Pin limit reached": {
                return "\u041c\u043e\u0436\u043d\u0430 \u0437\u0430\u043a\u0440\u0456\u043f\u0438\u0442\u0438 \u043c\u0430\u043a\u0441\u0438\u043c\u0443\u043c 5 \u043f\u0440\u043e\u0433\u0440\u0430\u043c";
            }
            case "Clean memory": {
                return "\u041e\u0447\u0438\u0441\u0442\u0438\u0442\u0438 \u043f\u0430\u043c\u2019\u044f\u0442\u044c";
            }
            case "RAM used": {
                return "RAM \u0432\u0438\u043a\u043e\u0440\u0438\u0441\u0442\u0430\u043d\u043e";
            }
            case "Launcher memory cleanup requested": {
                return "\u041e\u0447\u0438\u0449\u0435\u043d\u043d\u044f \u043f\u0430\u043c\u2019\u044f\u0442\u0456 \u043b\u0430\u0443\u043d\u0447\u0435\u0440\u0430 \u0437\u0430\u043f\u0443\u0449\u0435\u043d\u043e";
            }
            case "Choose wallpaper": {
                return "\u0412\u0438\u0431\u0440\u0430\u0442\u0438 \u0448\u043f\u0430\u043b\u0435\u0440\u0438";
            }
            case "Remove wallpaper": {
                return "\u041f\u0440\u0438\u0431\u0440\u0430\u0442\u0438 \u0448\u043f\u0430\u043b\u0435\u0440\u0438";
            }
            case "Launcher settings": {
                return "\u041d\u0430\u043b\u0430\u0448\u0442\u0443\u0432\u0430\u043d\u043d\u044f \u043b\u0430\u0443\u043d\u0447\u0435\u0440\u0430";
            }
            case "Interface language: English": {
                return "\u041c\u043e\u0432\u0430: \u0423\u043a\u0440\u0430\u0457\u043d\u0441\u044c\u043a\u0430 (\u0437\u043c\u0456\u043d\u0438\u0442\u0438)";
            }
            case "Animations: on": {
                return "\u0410\u043d\u0456\u043c\u0430\u0446\u0456\u0457: \u0443\u0432\u0456\u043c\u043a\u043d\u0435\u043d\u043e";
            }
            case "Animations: off": {
                return "\u0410\u043d\u0456\u043c\u0430\u0446\u0456\u0457: \u0432\u0438\u043c\u043a\u043d\u0435\u043d\u043e";
            }
            case "Open Android settings": {
                return "\u041d\u0430\u043b\u0430\u0448\u0442\u0443\u0432\u0430\u043d\u043d\u044f Android";
            }
            case "Refresh app list": {
                return "\u041e\u043d\u043e\u0432\u0438\u0442\u0438 \u0441\u043f\u0438\u0441\u043e\u043a \u043f\u0440\u043e\u0433\u0440\u0430\u043c";
            }
            case "No apps found": {
                return "\u041f\u0440\u043e\u0433\u0440\u0430\u043c\u0438 \u043d\u0435 \u0437\u043d\u0430\u0439\u0434\u0435\u043d\u0456";
            }
            case "Logs": {
                return "\u0416\u0443\u0440\u043d\u0430\u043b";
            }
            case "UI scale": {
                return "\u041c\u0430\u0441\u0448\u0442\u0430\u0431 UI";
            }
            case "Scan settings packages": {
                return "\u0417\u043d\u0430\u0439\u0442\u0438 \u043f\u0430\u043a\u0435\u0442\u0438 settings";
            }
            case "Clear logs": {
                return "\u041e\u0447\u0438\u0441\u0442\u0438\u0442\u0438 \u0436\u0443\u0440\u043d\u0430\u043b";
            }
            case "No log entries yet": {
                return "\u0417\u0430\u043f\u0438\u0441\u0456\u0432 \u0449\u0435 \u043d\u0435\u043c\u0430\u0454";
            }
            case "Wallpaper updated": {
                return "\u0428\u043f\u0430\u043b\u0435\u0440\u0438 \u043e\u043d\u043e\u0432\u043b\u0435\u043d\u043e";
            }
            case "Could not open wallpaper": {
                return "\u041d\u0435 \u0432\u0434\u0430\u043b\u043e\u0441\u044f \u0432\u0456\u0434\u043a\u0440\u0438\u0442\u0438 \u0437\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u043d\u044f";
            }
            case "Cancel": {
                return "\u0421\u043a\u0430\u0441\u0443\u0432\u0430\u0442\u0438";
            }
        }
        return this.localized(en);
    }

    private Locale localeForLanguage() {
        String tag = "en-US";
        if ("uk".equals(languageCode)) tag = "uk-UA";
        else if ("es".equals(languageCode)) tag = "es-ES";
        else if ("fr".equals(languageCode)) tag = "fr-FR";
        else if ("de".equals(languageCode)) tag = "de-DE";
        else if ("pl".equals(languageCode)) tag = "pl-PL";
        else if ("it".equals(languageCode)) tag = "it-IT";
        else if ("tr".equals(languageCode)) tag = "tr-TR";
        else if ("ar".equals(languageCode)) tag = "ar-SA";
        return Locale.forLanguageTag(tag);
    }

    private String localized(String key) {
        String[] v;
        switch (key) {
            case "Home": v=new String[]{"Home","Головна","Inicio","Accueil","Startseite","Start","Home","Ana Sayfa","الرئيسية"}; break;
            case "All apps": v=new String[]{"All apps","Усі програми","Todas las apps","Toutes les applis","Alle Apps","Wszystkie aplikacje","Tutte le app","Tüm uygulamalar","كل التطبيقات"}; break;
            case "Pinned apps": v=new String[]{"Pinned apps","Закріплені програми","Apps fijadas","Applis épinglées","Angeheftete Apps","Przypięte aplikacje","App fissate","Sabitlenen uygulamalar","التطبيقات المثبتة"}; break;
            case "Settings": v=new String[]{"Settings","Налаштування","Ajustes","Paramètres","Einstellungen","Ustawienia","Impostazioni","Ayarlar","الإعدادات"}; break;
            case "Set as default launcher": v=new String[]{"Set as default launcher","Зробити лаунчер типовим","Usar como launcher predeterminado","Définir comme lanceur par défaut","Als Standard-Launcher festlegen","Ustaw jako domyślny launcher","Imposta launcher predefinito","Varsayılan başlatıcı yap","تعيين كمشغّل افتراضي"}; break;
            case "Add app": v=new String[]{"Add app","Додати програму","Añadir app","Ajouter une appli","App hinzufügen","Dodaj aplikację","Aggiungi app","Uygulama ekle","إضافة تطبيق"}; break;
            case "Choose an app to pin": v=new String[]{"Choose an app to pin","Виберіть програму","Elige una app","Choisir une appli","App zum Anheften wählen","Wybierz aplikację","Scegli un’app","Sabitlemek için uygulama seç","اختر تطبيقًا للتثبيت"}; break;
            case "No pinned apps": v=new String[]{"No pinned apps","Немає закріплених програм","No hay apps fijadas","Aucune appli épinglée","Keine angehefteten Apps","Brak przypiętych aplikacji","Nessuna app fissata","Sabitlenmiş uygulama yok","لا توجد تطبيقات مثبتة"}; break;
            case "Pin limit reached": v=new String[]{"Pin limit reached","Досягнуто ліміту закріплень","Límite de apps fijadas alcanzado","Limite d’épingles atteint","Anheftlimit erreicht","Osiągnięto limit przypięć","Limite app fissate raggiunto","Sabitleme sınırına ulaşıldı","تم بلوغ حد التثبيت"}; break;
            case "Clean memory": v=new String[]{"Clean memory","Очистити пам’ять","Liberar memoria","Libérer la mémoire","Speicher freigeben","Wyczyść pamięć","Libera memoria","Belleği temizle","تنظيف الذاكرة"}; break;
            case "RAM used": v=new String[]{"RAM used","Використано RAM","RAM usada","RAM utilisée","RAM belegt","Użycie RAM","RAM utilizzata","Kullanılan RAM","استخدام الذاكرة"}; break;
            case "Launcher memory cleanup requested": v=new String[]{"Memory cleanup requested","Запущено очищення пам’яті","Limpieza de memoria solicitada","Nettoyage mémoire demandé","Speicherbereinigung angefordert","Zlecono czyszczenie pamięci","Pulizia memoria richiesta","Bellek temizliği istendi","تم طلب تنظيف الذاكرة"}; break;
            case "Choose wallpaper": v=new String[]{"Choose wallpaper","Вибрати шпалери","Elegir fondo","Choisir un fond d’écran","Hintergrund wählen","Wybierz tapetę","Scegli sfondo","Duvar kâğıdı seç","اختر خلفية"}; break;
            case "Remove wallpaper": v=new String[]{"Remove wallpaper","Прибрати шпалери","Quitar fondo","Supprimer le fond","Hintergrund entfernen","Usuń tapetę","Rimuovi sfondo","Duvar kâğıdını kaldır","إزالة الخلفية"}; break;
            case "Launcher settings": v=new String[]{"Launcher settings","Налаштування лаунчера","Ajustes del launcher","Paramètres du lanceur","Launcher-Einstellungen","Ustawienia launchera","Impostazioni launcher","Başlatıcı ayarları","إعدادات المشغّل"}; break;
            case "Animations: on": v=new String[]{"Animations: on","Анімації: увімкнено","Animaciones: activadas","Animations : activées","Animationen: an","Animacje: włączone","Animazioni: attive","Animasyonlar: açık","الحركات: مفعّلة"}; break;
            case "Animations: off": v=new String[]{"Animations: off","Анімації: вимкнено","Animaciones: desactivadas","Animations : désactivées","Animationen: aus","Animacje: wyłączone","Animazioni: disattivate","Animasyonlar: kapalı","الحركات: متوقفة"}; break;
            case "Open Android settings": v=new String[]{"Open Android settings","Відкрити налаштування Android","Abrir ajustes de Android","Ouvrir les paramètres Android","Android-Einstellungen öffnen","Otwórz ustawienia Androida","Apri impostazioni Android","Android ayarlarını aç","فتح إعدادات أندرويد"}; break;
            case "Refresh app list": v=new String[]{"Refresh app list","Оновити список програм","Actualizar apps","Actualiser les applis","App-Liste aktualisieren","Odśwież listę aplikacji","Aggiorna elenco app","Uygulama listesini yenile","تحديث قائمة التطبيقات"}; break;
            case "No apps found": v=new String[]{"No apps found","Програм не знайдено","No se encontraron apps","Aucune appli trouvée","Keine Apps gefunden","Nie znaleziono aplikacji","Nessuna app trovata","Uygulama bulunamadı","لم يتم العثور على تطبيقات"}; break;
            case "Logs": v=new String[]{"Logs","Журнал","Registros","Journaux","Protokolle","Dziennik","Registri","Günlükler","السجلات"}; break;
            case "UI scale": v=new String[]{"UI scale","Масштаб інтерфейсу","Escala de interfaz","Échelle de l’interface","UI-Skalierung","Skala interfejsu","Scala interfaccia","Arayüz ölçeği","حجم الواجهة"}; break;
            case "Scan settings packages": v=new String[]{"Scan settings packages","Знайти пакети налаштувань","Buscar paquetes de ajustes","Rechercher les paquets de paramètres","Einstellungspakete suchen","Szukaj pakietów ustawień","Cerca pacchetti impostazioni","Ayar paketlerini tara","فحص حزم الإعدادات"}; break;
            case "Clear logs": v=new String[]{"Clear logs","Очистити журнал","Borrar registros","Effacer les journaux","Protokolle löschen","Wyczyść dziennik","Cancella registri","Günlükleri temizle","مسح السجلات"}; break;
            case "No log entries yet": v=new String[]{"No log entries yet","Записів ще немає","Aún no hay registros","Aucun journal pour l’instant","Noch keine Protokolleinträge","Brak wpisów","Nessun registro","Henüz günlük kaydı yok","لا توجد سجلات بعد"}; break;
            case "Wallpaper updated": v=new String[]{"Wallpaper updated","Шпалери оновлено","Fondo actualizado","Fond mis à jour","Hintergrund aktualisiert","Tapeta zaktualizowana","Sfondo aggiornato","Duvar kâğıdı güncellendi","تم تحديث الخلفية"}; break;
            case "Could not open wallpaper": v=new String[]{"Could not open wallpaper","Не вдалося відкрити зображення","No se pudo abrir la imagen","Impossible d’ouvrir l’image","Bild konnte nicht geöffnet werden","Nie można otworzyć obrazu","Impossibile aprire l’immagine","Görsel açılamadı","تعذّر فتح الخلفية"}; break;
            case "Cancel": v=new String[]{"Cancel","Скасувати","Cancelar","Annuler","Abbrechen","Anuluj","Annulla","İptal","إلغاء"}; break;
            case "Welcome to LightLauncher": v=new String[]{"Welcome to LightLauncher","Вітаємо у LightLauncher","Te damos la bienvenida a LightLauncher","Bienvenue dans LightLauncher","Willkommen bei LightLauncher","Witamy w LightLauncher","Benvenuto in LightLauncher","LightLauncher’a hoş geldin","مرحبًا بك في LightLauncher"}; break;
            case "Choose language and customize your launcher.": v=new String[]{"Choose language and customize your launcher.","Оберіть мову та налаштуйте лаунчер.","Elige el idioma y personaliza el launcher.","Choisissez la langue et personnalisez le lanceur.","Wähle Sprache und passe den Launcher an.","Wybierz język i dostosuj launcher.","Scegli la lingua e personalizza il launcher.","Dili seç ve başlatıcıyı özelleştir.","اختر اللغة وخصص المشغّل."}; break;
            case "Language": v=new String[]{"Language","Мова","Idioma","Langue","Sprache","Język","Lingua","Dil","اللغة"}; break;
            case "Built-in wallpapers": v=new String[]{"Built-in wallpapers","Вбудовані шпалери","Fondos incluidos","Fonds intégrés","Enthaltene Hintergründe","Wbudowane tapety","Sfondi inclusi","Dahili duvar kâğıtları","خلفيات مضمّنة"}; break;
            case "Choose image from storage": v=new String[]{"Choose image from storage","Вибрати зображення з пам’яті","Elegir imagen del almacenamiento","Choisir une image du stockage","Bild aus Speicher wählen","Wybierz obraz z pamięci","Scegli immagine dalla memoria","Depolamadan görsel seç","اختر صورة من التخزين"}; break;
            case "Back": v=new String[]{"Back","Назад","Atrás","Retour","Zurück","Wstecz","Indietro","Geri","رجوع"}; break;
            case "Next": v=new String[]{"Next","Далі","Siguiente","Suivant","Weiter","Dalej","Avanti","İleri","التالي"}; break;
            case "Finish": v=new String[]{"Finish","Готово","Terminar","Terminer","Fertig","Zakończ","Fine","Bitir","إنهاء"}; break;
            case "Set up LightLauncher": v=new String[]{"Set up LightLauncher","Налаштуйте LightLauncher","Configura LightLauncher","Configurer LightLauncher","LightLauncher einrichten","Skonfiguruj LightLauncher","Configura LightLauncher","LightLauncher’ı kur","إعداد LightLauncher"}; break;
            case "FAQ": v=new String[]{"FAQ","Довідка","Ayuda","Aide","Hilfe","Pomoc","Aiuto","Yardım","الأسئلة الشائعة"}; break;
            case "wallpaper_1": v=new String[]{"Turquoise Alpine Lake","Бірюзове альпійське озеро","Lago alpino turquesa","Lac alpin turquoise","Türkiser Bergsee","Turkusowe jezioro alpejskie","Lago alpino turchese","Turkuaz dağ gölü","بحيرة جبلية فيروزية"}; break;
            case "wallpaper_2": v=new String[]{"Pink Dawn Lake","Рожевий світанок над озером","Lago al amanecer rosa","Lac à l’aube rose","See im rosigen Morgenlicht","Jezioro o różowym świcie","Lago all’alba rosa","Pembe şafak gölü","بحيرة عند الفجر الوردي"}; break;
            case "wallpaper_3": v=new String[]{"Green Mountain Valley","Зелена гірська долина","Valle verde de montaña","Vallée de montagne verdoyante","Grünes Bergtal","Zielona górska dolina","Valle verde di montagna","Yeşil dağ vadisi","وادي الجبال الأخضر"}; break;
            case "wallpaper_4": v=new String[]{"Tropical Beach","Тропічний пляж","Playa tropical","Plage tropicale","Tropischer Strand","Tropikalna plaża","Spiaggia tropicale","Tropik plaj","شاطئ استوائي"}; break;
            case "wallpaper_5": v=new String[]{"Autumn Lane","Осіння алея","Camino otoñal","Chemin d’automne","Herbstweg","Jesienna ścieżka","Sentiero d’autunno","Sonbahar yolu","طريق الخريف"}; break;
            case "wallpaper_6": v=new String[]{"Puppies by the Sea","Цуценята біля моря","Cachorros junto al mar","Chiots au bord de la mer","Welpen am Meer","Szczeniaki nad morzem","Cuccioli in riva al mare","Deniz kenarında yavrular","جراء عند البحر"}; break;
            case "wallpaper_7": v=new String[]{"Evergreen Mountains","Вічнозелені гори","Montañas siempre verdes","Montagnes verdoyantes","Immergrüne Berge","Zielone górskie lasy","Montagne sempreverdi","Yemyeşil dağlar","جبال دائمة الخضرة"}; break;
            case "faq_q1": v=new String[]{"How do I pin an app?","Як закріпити програму?","¿Cómo fijo una app?","Comment épingler une appli ?","Wie hefte ich eine App an?","Jak przypiąć aplikację?","Come fisso un’app?","Bir uygulamayı nasıl sabitlerim?","كيف أثبّت تطبيقًا؟"}; break;
            case "faq_a1": v=new String[]{"Long-press an app tile in All apps. You can pin up to five.","Натисніть і утримуйте програму в списку. Можна закріпити до п’яти.","Mantén pulsada una app en Todas las apps. Puedes fijar hasta cinco.","Maintenez une appli dans Toutes les applis. Vous pouvez en épingler cinq.","Halte eine App in Alle Apps gedrückt. Du kannst bis zu fünf anheften.","Przytrzymaj aplikację na liście. Możesz przypiąć maksymalnie pięć.","Tieni premuta un’app in Tutte le app. Puoi fissarne fino a cinque.","Tüm uygulamalar’da bir uygulamaya basılı tut. En fazla beş tane sabitleyebilirsin.","اضغط مطولًا على التطبيق في القائمة. يمكنك تثبيت خمسة تطبيقات كحد أقصى."}; break;
            case "faq_q2": v=new String[]{"How do I change the wallpaper?","Як змінити шпалери?","¿Cómo cambio el fondo?","Comment changer le fond d’écran ?","Wie ändere ich den Hintergrund?","Jak zmienić tapetę?","Come cambio lo sfondo?","Duvar kâğıdını nasıl değiştiririm?","كيف أغيّر الخلفية؟"}; break;
            case "faq_a2": v=new String[]{"Open Settings, choose Built-in wallpapers, then select one or browse storage.","Відкрийте Налаштування → Вбудовані шпалери та виберіть зображення або файл.","Abre Ajustes → Fondos incluidos y elige una imagen o busca en el almacenamiento.","Ouvrez Paramètres → Fonds intégrés et choisissez une image ou parcourez le stockage.","Öffne Einstellungen → Enthaltene Hintergründe und wähle ein Bild oder den Speicher.","Otwórz Ustawienia → Wbudowane tapety i wybierz obraz albo przeglądaj pliki.","Apri Impostazioni → Sfondi inclusi e scegli un’immagine o sfoglia i file.","Ayarlar → Dahili duvar kâğıtları’nı açıp görsel seç veya depolamaya göz at.","افتح الإعدادات ثم الخلفيات المضمّنة واختر صورة أو تصفح التخزين."}; break;
            case "faq_q3": v=new String[]{"How do I set LightLauncher as Home?","Як зробити LightLauncher основним?","¿Cómo uso LightLauncher como inicio?","Comment définir LightLauncher comme accueil ?","Wie stelle ich LightLauncher als Startseite ein?","Jak ustawić LightLauncher jako ekran główny?","Come imposto LightLauncher come Home?","LightLauncher’ı ana ekran nasıl yaparım?","كيف أعيّن LightLauncher كشاشة رئيسية؟"}; break;
            case "faq_a3": v=new String[]{"Choose Set as default launcher or select it in Android Home settings.","Виберіть Основний лаунчер у налаштуваннях або LightLauncher у параметрах головного екрана Android.","Elige Usar como predeterminado o selecciónalo en los ajustes de inicio de Android.","Choisissez Définir par défaut ou LightLauncher dans les réglages d’accueil Android.","Wähle Als Standard festlegen oder LightLauncher in den Android-Startseiteneinstellungen.","Wybierz Ustaw jako domyślny albo LightLauncher w ustawieniach ekranu głównego Androida.","Scegli Imposta predefinito o LightLauncher nelle impostazioni Home di Android.","Varsayılan yap’ı seç veya Android Ana ekran ayarlarında LightLauncher’ı seç.","اختر التعيين كمشغّل افتراضي أو حدده في إعدادات الشاشة الرئيسية في أندرويد."}; break;
            case "faq_q4": v=new String[]{"What do the Wi-Fi colors mean?","Що означають кольори Wi-Fi?","¿Qué significan los colores del Wi-Fi?","Que signifient les couleurs du Wi-Fi ?","Was bedeuten die WLAN-Farben?","Co oznaczają kolory Wi-Fi?","Cosa indicano i colori del Wi-Fi?","Wi-Fi renkleri ne anlama geliyor?","ماذا تعني ألوان الواي فاي؟"}; break;
            case "faq_a4": v=new String[]{"Green means online, yellow means internet is unverified, and red means disconnected.","Зелений — є інтернет, жовтий — інтернет не підтверджено, червоний — немає з’єднання.","Verde: internet disponible; amarillo: internet no confirmado; rojo: desconectado.","Vert : internet disponible ; jaune : internet non confirmé ; rouge : déconnecté.","Grün: online; Gelb: Internet nicht bestätigt; Rot: getrennt.","Zielony: internet działa; żółty: brak potwierdzonego internetu; czerwony: rozłączono.","Verde: online; giallo: internet non verificato; rosso: disconnesso.","Yeşil: internet var; sarı: internet doğrulanmadı; kırmızı: bağlantı yok.","الأخضر يعني اتصالًا بالإنترنت، والأصفر إنترنت غير مؤكد، والأحمر يعني عدم الاتصال."}; break;
            case "faq_q5": v=new String[]{"What does Clean memory do?","Що робить очищення пам’яті?","¿Qué hace Liberar memoria?","À quoi sert Libérer la mémoire ?","Was macht Speicher freigeben?","Co robi czyszczenie pamięci?","Cosa fa Libera memoria?","Belleği temizle ne yapar?","ماذا يفعل تنظيف الذاكرة؟"}; break;
            case "faq_a5": v=new String[]{"It asks Android to stop app processes where allowed. Essential services may remain.","Функція просить Android зупинити фонові процеси, якщо це дозволено. Системні служби залишаються.","Pide a Android detener procesos permitidos. Los servicios esenciales pueden seguir activos.","Android est invité à arrêter les processus autorisés. Les services essentiels peuvent rester actifs.","Android wird gebeten, erlaubte App-Prozesse zu beenden. Wichtige Dienste können weiterlaufen.","Prosi Androida o zatrzymanie dozwolonych procesów. Ważne usługi mogą nadal działać.","Chiede ad Android di fermare i processi consentiti. I servizi essenziali peuvent rester actifs.","Android’dan izin verilen uygulama işlemlerini durdurmasını ister. Gerekli hizmetler kalabilir.","يطلب من أندرويد إيقاف العمليات المسموح بها، وقد تبقى الخدمات الأساسية قيد التشغيل."}; break;
            case "faq_q6": v=new String[]{"How do I resize the interface?","Як змінити розмір інтерфейсу?","¿Cómo cambio el tamaño de la interfaz?","Comment modifier la taille de l’interface ?","Wie ändere ich die Oberflächengröße?","Jak zmienić rozmiar interfejsu?","Come ridimensiono l’interfaccia?","Arayüz boyutunu nasıl değiştiririm?","كيف أغيّر حجم الواجهة؟"}; break;
            case "faq_a6": v=new String[]{"Use UI scale in Settings to cycle through sizes.","Використовуйте Масштаб інтерфейсу в налаштуваннях.","Usa Escala de interfaz en Ajustes para cambiar el tamaño.","Utilisez Échelle de l’interface dans Paramètres.","Nutze UI-Skalierung in den Einstellungen.","Użyj Skali interfejsu w Ustawieniach.","Usa Scala interfaccia nelle Impostazioni.","Boyutu değiştirmek için Ayarlar’daki Arayüz ölçeği’ni kullan.","استخدم حجم الواجهة في الإعدادات لتغيير الحجم."}; break;
            default: return key;
        }
        int index = 0;
        for (int i=0;i<LANG_CODES.length;i++) if (LANG_CODES[i].equals(languageCode)) index=i;
        return v[index];
    }

    private String arabicText(String key) {
        switch (key) {
            case "Home": return "الرئيسية";
            case "All apps": return "كل التطبيقات";
            case "Pinned apps": return "التطبيقات المثبتة";
            case "Settings": return "الإعدادات";
            case "Set as default launcher": return "تعيين كمشغّل افتراضي";
            case "Add app": return "إضافة تطبيق";
            case "Choose an app to pin": return "اختر تطبيقًا لتثبيته";
            case "No pinned apps": return "لا توجد تطبيقات مثبتة";
            case "Pin limit reached": return "يمكن تثبيت خمسة تطبيقات كحد أقصى";
            case "Clean memory": return "تنظيف الذاكرة";
            case "RAM used": return "استخدام الذاكرة";
            case "Launcher memory cleanup requested": return "تم طلب تنظيف ذاكرة المشغّل";
            case "Choose wallpaper": return "اختر خلفية";
            case "Remove wallpaper": return "إزالة الخلفية";
            case "Launcher settings": return "إعدادات المشغّل";
            case "Interface language: English": return this.languageLabel();
            case "Animations: on": return "الحركات: مفعّلة";
            case "Animations: off": return "الحركات: متوقفة";
            case "Open Android settings": return "فتح إعدادات أندرويد";
            case "Refresh app list": return "تحديث قائمة التطبيقات";
            case "No apps found": return "لم يتم العثور على تطبيقات";
            case "Logs": return "السجلات";
            case "UI scale": return "حجم الواجهة";
            case "Scan settings packages": return "فحص حزم الإعدادات";
            case "Clear logs": return "مسح السجلات";
            case "No log entries yet": return "لا توجد سجلات بعد";
            case "Wallpaper updated": return "تم تحديث الخلفية";
            case "Could not open wallpaper": return "تعذّر فتح الخلفية";
            case "Cancel": return "إلغاء";
            default: return this.localized(key);
        }
    }

    private String languageLabel() {
        int idx=0; for(int i=0;i<LANG_CODES.length;i++)if(LANG_CODES[i].equals(languageCode))idx=i;
        return this.t("Language")+": "+LANG_NAMES[idx];
    }

    private String t(String key) {
        return this.s("", key);
    }

    private int dp(float v) {
        return (int)(v * this.getResources().getDisplayMetrics().density * (float)this.uiScale / 100.0f + 0.5f);
    }

    private void buildShell() {
        this.scene = new FrameLayout((Context)this);
        this.scene.setBackgroundColor(-15394270);
        this.wallpaper = new ImageView((Context)this);
        this.wallpaper.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.scene.addView((View)this.wallpaper, (ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-1, -1));
        View shade = new View((Context)this);
        shade.setBackgroundColor(1426920475);
        this.scene.addView(shade, (ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-1, -1));
        this.root = new LinearLayout((Context)this);
        this.root.setOrientation(1);
        this.root.setPadding(this.dp(34.0f), this.dp(18.0f), this.dp(34.0f), this.dp(18.0f));
        this.root.setBackgroundColor(-1726999267);
        this.root.setClipChildren(false);
        this.root.setClipToPadding(false);
        this.root.setLayoutDirection("ar".equals(this.languageCode) ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        LinearLayout top = new LinearLayout((Context)this);
        top.setGravity(16);
        top.setOrientation(0);
        LinearLayout time = new LinearLayout((Context)this);
        time.setOrientation(1);
        this.clock = this.label("--:--", 48, -657415, true);
        this.dateLine = this.label("", 13, -4142640, false);
        time.addView((View)this.clock);
        time.addView((View)this.dateLine);
        top.addView((View)time, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(0, -2, 1.0f));
        this.wifiIcon = new StatusIconView((Context)this);
        top.addView((View)this.wifiIcon, (ViewGroup.LayoutParams)this.statusParams());
        Button settingsIcon = this.iconButton("\u2699", this.t("Settings"));
        settingsIcon.setOnClickListener(v -> this.showPage("settings"));
        LinearLayout.LayoutParams sip = this.circleParams();
        sip.leftMargin = this.dp(8.0f);
        top.addView((View)settingsIcon, (ViewGroup.LayoutParams)sip);
        Button add = this.iconButton("+", this.t("Add app"));
        add.setTextSize((float)(23 * this.uiScale) / 100.0f);
        add.setOnClickListener(v -> this.showAddAppDialog());
        LinearLayout.LayoutParams addp = this.circleParams();
        addp.leftMargin = this.dp(8.0f);
        top.addView((View)add, (ViewGroup.LayoutParams)addp);
        this.root.addView((View)top);
        this.content = new LinearLayout((Context)this);
        this.content.setOrientation(1);
        this.content.setClipChildren(false);
        this.content.setClipToPadding(false);
        this.content.setBackgroundColor(0);
        this.root.addView((View)this.content, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(-1, 0, 1.0f));
        this.selectionHint = this.label("", 12, -4142640, false);
        this.scene.addView((View)this.root, (ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-1, -1));
        this.setContentView((View)this.scene);
        this.applyWallpaper();
    }

    private LinearLayout.LayoutParams statusParams() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(this.dp(48.0f), this.dp(48.0f));
        p.leftMargin = this.dp(5.0f);
        return p;
    }

    private LinearLayout.LayoutParams toolParams() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-2, this.dp(44.0f));
        p.rightMargin = this.dp(8.0f);
        return p;
    }

    private LinearLayout.LayoutParams circleParams() {
        return new LinearLayout.LayoutParams(this.dp(54.0f), this.dp(54.0f));
    }

    private Button iconButton(String glyph, String description) {
        Button b = this.button(glyph);
        b.setTextSize((float)(19 * this.uiScale) / 100.0f);
        b.setContentDescription((CharSequence)description);
        b.setBackground((Drawable)this.round(-2144849605, this.dp(28.0f)));
        b.setOnFocusChangeListener((v, f) -> v.setBackground((Drawable)this.round(f ? -1724099998 : -2144849605, this.dp(28.0f))));
        return b;
    }

    private TextView label(String text, int size, int color2, boolean bold) {
        TextView v = new TextView((Context)this);
        v.setText((CharSequence)text);
        v.setTextSize((float)(size * this.uiScale) / 100.0f);
        v.setTextColor(color2);
        if (bold) {
            v.setTypeface(Typeface.DEFAULT, 1);
        }
        return v;
    }

    private Button button(String text) {
        Button b = new Button((Context)this);
        b.setText((CharSequence)text);
        b.setTextColor(-657415);
        b.setTextSize((float)(15 * this.uiScale) / 100.0f);
        b.setAllCaps(false);
        b.setFocusable(true);
        b.setBackground((Drawable)this.round(-2144849605, this.dp(14.0f)));
        b.setOnFocusChangeListener((v, f) -> v.setBackground((Drawable)this.round(f ? -1724099998 : -2144849605, this.dp(14.0f))));
        return b;
    }

    private GradientDrawable round(int color2, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color2);
        d.setCornerRadius((float)radius);
        d.setStroke(this.dp(1.0f), color2 == -1724099998 ? -8857652 : 1431199081);
        return d;
    }

    private void showPage(String which) {
        this.page = which;
        this.content.removeAllViews();
        this.content.setBackgroundColor(0);
        this.content.invalidate();
        this.content.requestLayout();
        this.selected = null;
        this.selectionHint.setText((CharSequence)"");
        if (which.equals("settings")) {
            this.showSettings();
            return;
        }
        if (which.equals("setup")) {
            this.showSetup();
            return;
        }
        if (which.equals("wallpapers")) {
            this.showWallpapers();
            return;
        }
        if (which.equals("faq")) {
            this.showFaq();
            return;
        }
        if (which.equals("logs")) {
            this.showLogs();
            return;
        }
        if (which.equals("apps")) {
            this.showAllApps();
            return;
        }
        this.showHome();
    }

    private LinearLayout createPagePanel(String title) {
        ScrollView scroll = new ScrollView((Context)this);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout panel = new LinearLayout((Context)this);
        panel.setOrientation(1);
        panel.setPadding(this.dp(20.0f), this.dp(16.0f), this.dp(20.0f), this.dp(20.0f));
        panel.setBackground((Drawable)this.round(-2144849605, this.dp(20.0f)));
        scroll.addView((View)panel);
        this.content.addView((View)scroll, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(-1, -1));
        panel.addView((View)this.label(title, 20, -657415, true));
        return panel;
    }

    private void addPageButton(LinearLayout panel, String title, Runnable action) {
        Button b=this.button(title);
        b.setOnClickListener(v->action.run());
        panel.addView((View)b,(ViewGroup.LayoutParams)this.row());
    }

    private void showSetup() {
        LinearLayout panel=createPagePanel(this.t("Set up LightLauncher"));
        panel.addView((View)this.label(this.t("Welcome to LightLauncher"),27,-657415,true));
        panel.addView((View)this.label(this.t("Choose language and customize your launcher."),16,-4142640,false));
        if(setupStep==0){
            addPageButton(panel,this.languageLabel(),()->chooseLanguage());
        } else if(setupStep==1){
            addPageButton(panel,this.t("UI scale")+": "+this.uiScale+"%",()->{this.uiScale+=10;if(this.uiScale>130)this.uiScale=80;this.getPreferences(0).edit().putInt("ui_scale",this.uiScale).apply();this.showPage("setup");});
            addPageButton(panel,this.t("Built-in wallpapers"),()->{this.wallpaperReturnPage="setup";this.showPage("wallpapers");});
        } else {
            addPageButton(panel,this.t("Set as default launcher"),()->this.openSystemSettings("android.settings.HOME_SETTINGS"));
            addPageButton(panel,this.t("FAQ"),()->{faqReturnPage="setup";this.showPage("faq");});
        }
        LinearLayout nav=new LinearLayout((Context)this);nav.setOrientation(0);
        if(setupStep>0){Button back=this.button(this.t("Back"));back.setOnClickListener(v->{setupStep--;showPage("setup");});nav.addView((View)back,new LinearLayout.LayoutParams(0,this.dp(56),1));}
        Button next=this.button(setupStep<2?this.t("Next"):this.t("Finish"));
        next.setOnClickListener(v->{if(setupStep<2){setupStep++;showPage("setup");}else{this.getPreferences(0).edit().putBoolean("setup_complete",true).apply();showPage("pinned");}});
        LinearLayout.LayoutParams np=new LinearLayout.LayoutParams(0,this.dp(56),1);np.leftMargin=this.dp(8);nav.addView((View)next,np);
        LinearLayout.LayoutParams navp=new LinearLayout.LayoutParams(-1,this.dp(56));navp.topMargin=this.dp(10);panel.addView((View)nav,navp);
    }

    private void chooseLanguage() {
        new AlertDialog.Builder((Context)this).setTitle((CharSequence)this.t("Language")).setSingleChoiceItems(LANG_NAMES,Math.max(0,Arrays.asList(LANG_CODES).indexOf(languageCode)),(dialog,index)->{
            languageCode=LANG_CODES[index];ukrainian="uk".equals(languageCode);arabic="ar".equals(languageCode);
            this.getPreferences(0).edit().putString("language",languageCode).putBoolean("uk",ukrainian).putBoolean("ar",arabic).apply();
            dialog.dismiss();this.buildShell();this.showPage(page);
        }).setNegativeButton((CharSequence)this.t("Cancel"),null).show();
    }

    private void showWallpapers() {
        LinearLayout panel=createPagePanel(this.t("Built-in wallpapers"));
        LinearLayout row=null;int index=0;String[] files;
        try{files=this.getAssets().list("wallpapers");Arrays.sort(files);}catch(Exception e){files=new String[0];}
        for(String file:files){
            if(row==null||index%2==0){row=new LinearLayout((Context)this);row.setOrientation(0);panel.addView((View)row,new LinearLayout.LayoutParams(-1,-2));}
            final String name=file;LinearLayout tile=new LinearLayout((Context)this);tile.setOrientation(1);tile.setFocusable(true);tile.setClickable(true);tile.setPadding(this.dp(6),this.dp(6),this.dp(6),this.dp(6));tile.setBackground((Drawable)this.round(-2144849605,this.dp(14)));
            ImageView image=new ImageView((Context)this);image.setScaleType(ImageView.ScaleType.FIT_CENTER);image.setBackgroundColor(0x55252e40);image.setClipToOutline(true);image.setImageBitmap(this.decodeAssetWallpaper(name,2));tile.addView((View)image,new LinearLayout.LayoutParams(-1,this.dp(132)));
            TextView label=this.label(this.wallpaperDisplayName(name),13,-657415,false);label.setGravity(17);tile.addView((View)label,new LinearLayout.LayoutParams(-1,this.dp(36)));
            LinearLayout.LayoutParams tp=new LinearLayout.LayoutParams(0,-2,1);tp.setMargins(this.dp(4),this.dp(4),this.dp(4),this.dp(4));row.addView((View)tile,tp);
            tile.setOnFocusChangeListener((v,focused)->v.setBackground((Drawable)this.round(focused?-1724099998:-2144849605,this.dp(14))));
            tile.setOnClickListener(v->{this.getPreferences(0).edit().putString("wallpaper","asset:"+name).apply();this.applyWallpaper();this.showPage(wallpaperReturnPage);});index++;
        }
        addPageButton(panel,this.t("Choose image from storage"),()->this.pickWallpaper());
        addPageButton(panel,this.t("Remove wallpaper"),()->{this.getPreferences(0).edit().remove("wallpaper").apply();this.applyWallpaper();this.showPage(wallpaperReturnPage);});
        addPageButton(panel,this.t("Back"),()->this.showPage(wallpaperReturnPage));
    }

    private String wallpaperDisplayName(String file) {
        if(file.matches("image[1-7]\\.jpg")){
            int number=Integer.parseInt(file.substring(5,file.indexOf('.')));
            return this.t("wallpaper_"+number);
        }
        String base=file.replaceFirst("(?i)\\.[^.]+$","").replace('_',' ').replace('-',' ');
        return base.substring(0,1).toUpperCase(localeForLanguage())+base.substring(1);
    }

    private Bitmap decodeAssetWallpaper(String name,int sample) {
        try{InputStream in=this.getAssets().open("wallpapers/"+name);BitmapFactory.Options o=new BitmapFactory.Options();o.inSampleSize=sample;Bitmap b=BitmapFactory.decodeStream(in,null,o);in.close();return b;}catch(Exception e){return null;}
    }

    private void showFaq() {
        LinearLayout panel=createPagePanel(this.t("FAQ"));
        String[][] qa={{"How do I pin an app?","Long-press an app tile in All apps. You can pin up to five."},{"How do I change the wallpaper?","Open Settings, choose Built-in wallpapers, then select one or browse storage."},{"How do I set LightLauncher as Home?","Choose Set as default launcher or select it in Android Home settings."},{"What do the Wi-Fi colors mean?","Green means online, yellow means internet is unverified, and red means disconnected."},{"What does Clean memory do?","It asks Android to stop other app processes where allowed. Essential services can remain."},{"How do I resize the interface?","Use UI scale in Settings to cycle through sizes."}};
        for(int i=0;i<qa.length;i++){final int n=i+1;Button q=this.button(this.t("faq_q"+n));q.setGravity(android.view.Gravity.START|android.view.Gravity.CENTER_VERTICAL);q.setPadding(this.dp(18),0,this.dp(12),0);LinearLayout.LayoutParams qp=this.row();qp.leftMargin=this.dp(10);qp.rightMargin=this.dp(10);panel.addView((View)q,(ViewGroup.LayoutParams)qp);TextView a=this.label(this.t("faq_a"+n),14,-4142640,false);a.setPadding(this.dp(24),this.dp(8),this.dp(12),this.dp(8));a.setVisibility(View.GONE);LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,-2);ap.leftMargin=this.dp(10);ap.rightMargin=this.dp(10);panel.addView((View)a,ap);q.setOnClickListener(v->a.setVisibility(a.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE));}
        addPageButton(panel,this.t("Back"),()->this.showPage(faqReturnPage));
    }

    private void showHome() {
        TextView heading = this.label(this.t("Pinned apps"), 18, -657415, true);
        heading.setPadding(0, this.dp(8.0f), 0, this.dp(8.0f));
        this.content.addView((View)heading);
        Set<String> pins = this.getPins();
        ArrayList<AppEntry> chosen = new ArrayList<AppEntry>();
        for (AppEntry a : this.apps) {
            if (!pins.contains(a.pkg) || chosen.size() >= 5) continue;
            chosen.add(a);
        }
        HorizontalScrollView railScroll = new HorizontalScrollView((Context)this);
        railScroll.setHorizontalScrollBarEnabled(false);
        railScroll.setClipToPadding(false);
        railScroll.setClipChildren(false);
        railScroll.setPadding(0, this.dp(3), 0, this.dp(3));
        LinearLayout rail = new LinearLayout((Context)this);
        rail.setGravity(16);
        rail.setClipChildren(false);
        rail.setPadding(this.dp(16.0f), this.dp(16.0f), this.dp(20.0f), this.dp(20.0f));
        for (AppEntry a : chosen) {
            View tile = this.createTile(a);
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(this.dp(198.0f), this.dp(164.0f));
            tp.rightMargin = this.dp(12.0f);
            rail.addView(tile, (ViewGroup.LayoutParams)tp);
        }
        View memoryTile = this.createCleanMemoryTile();
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(this.dp(198.0f), this.dp(164.0f));
        mp.rightMargin = this.dp(12.0f);
        rail.addView(memoryTile, (ViewGroup.LayoutParams)mp);
        View allTile = this.createAllAppsTile();
        LinearLayout.LayoutParams allp = new LinearLayout.LayoutParams(this.dp(198.0f), this.dp(164.0f));
        allp.rightMargin = this.dp(12.0f);
        rail.addView(allTile, (ViewGroup.LayoutParams)allp);
        railScroll.addView((View)rail);
        this.content.addView((View)railScroll, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(-1, this.dp(212.0f)));
        if (chosen.isEmpty()) {
            TextView empty = this.label(this.t("No pinned apps"), 14, -4142640, false);
            empty.setPadding(0, this.dp(4.0f), 0, 0);
            this.content.addView((View)empty);
        }
    }

    private View createCleanMemoryTile() {
        LinearLayout tile = new LinearLayout((Context)this);
        tile.setOrientation(1);
        tile.setGravity(17);
        tile.setFocusable(true);
        tile.setClickable(true);
        tile.setPadding(this.dp(8.0f), this.dp(9.0f), this.dp(8.0f), this.dp(7.0f));
        tile.setBackground((Drawable)this.round(-2144849605, this.dp(18.0f)));
        TextView icon = this.label("RAM", 19, -8857652, true);
        icon.setGravity(17);
        tile.addView((View)icon, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(this.dp(70.0f), this.dp(40.0f)));
        TextView name = this.label(this.t("Clean memory"), 15, -657415, true);
        name.setGravity(17);
        tile.addView((View)name);
        this.ramPercent = this.label("--%", 14, -4142640, false);
        this.ramPercent.setGravity(17);
        tile.addView((View)this.ramPercent);
        tile.setContentDescription((CharSequence)this.t("Clean memory"));
        tile.setOnFocusChangeListener((v, f) -> tile.setBackground((Drawable)this.round(f ? -1724099998 : -2144849605, this.dp(18.0f))));
        tile.setOnClickListener(v -> this.cleanLauncherMemory());
        return tile;
    }

    private void updateRamDisplay() {
        if (this.ramPercent == null) {
            return;
        }
        try {
            ActivityManager am = (ActivityManager)this.getSystemService("activity");
            ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(info);
            long total = info.totalMem;
            long used = total - info.availMem;
            int pct = total > 0L ? (int)(used * 100L / total) : 0;
            this.ramPercent.setText((CharSequence)(this.t("RAM used") + " " + pct + "%"));
        }
        catch (Exception e) {
            this.ramPercent.setText((CharSequence)"RAM --%");
        }
    }

    private void cleanLauncherMemory() {
        int attempted = 0;
        try {
            ActivityManager am = (ActivityManager)this.getSystemService("activity");
            for (ApplicationInfo app : this.getPackageManager().getInstalledApplications(0)) {
                if (app.packageName.equals(this.getPackageName()) || (app.flags & 1) != 0) continue;
                try {
                    am.killBackgroundProcesses(app.packageName);
                    ++attempted;
                }
                catch (Exception exception) {}
            }
        }
        catch (Exception e) {
            this.appendSettingsLog("Background cleanup failed: " + e.getClass().getSimpleName());
        }
        System.runFinalization();
        System.gc();
        this.appendSettingsLog("Requested background-process cleanup for " + attempted + " user apps");
        int count = attempted;
        this.handler.postDelayed(() -> this.updateRamDisplay(), 1200L);
        Toast.makeText((Context)this, (CharSequence)("Requested background cleanup for " + count + " apps. Android may restart background services."), (int)1).show();
    }

    private View createAllAppsTile() {
        LinearLayout tile = new LinearLayout((Context)this);
        tile.setOrientation(1);
        tile.setGravity(17);
        tile.setFocusable(true);
        tile.setClickable(true);
        tile.setPadding(this.dp(8.0f), this.dp(10.0f), this.dp(8.0f), this.dp(8.0f));
        tile.setBackground((Drawable)this.round(-2144849605, this.dp(18.0f)));
        AppGridIcon icon = new AppGridIcon((Context)this);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(this.dp(70.0f), this.dp(70.0f));
        ip.bottomMargin = this.dp(7.0f);
        tile.addView((View)icon, (ViewGroup.LayoutParams)ip);
        TextView name = this.label(this.t("All apps"), 15, -657415, true);
        name.setGravity(17);
        tile.addView((View)name);
        tile.setContentDescription((CharSequence)this.t("All apps"));
        tile.setOnFocusChangeListener((v, f) -> tile.setBackground((Drawable)this.round(f ? -1724099998 : -2144849605, this.dp(18.0f))));
        tile.setOnClickListener(v -> this.showPage("apps"));
        return tile;
    }

    private void showAllApps() {
        GridView grid;
        if (this.apps.isEmpty()) {
            this.content.addView((View)this.label(this.t("No apps found"), 18, -4142640, false));
            return;
        }
        this.appGrid = grid = new GridView((Context)this);
        grid.setNumColumns(4);
        grid.setHorizontalSpacing(this.dp(18.0f));
        grid.setVerticalSpacing(this.dp(18.0f));
        grid.setPadding(this.dp(20.0f), this.dp(20.0f), this.dp(20.0f), this.dp(22.0f));
        grid.setClipToPadding(false);
        grid.setClipChildren(false);
        grid.setSelector(17170445);
        grid.setAdapter((ListAdapter)new AppAdapter());
        grid.setOnItemClickListener((p, v, pos, id) -> this.launch(this.apps.get(pos)));
        grid.setOnItemLongClickListener((p, v, pos, id) -> {
            this.togglePin(this.apps.get(pos));
            return true;
        });
        this.content.addView((View)grid, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(-1, -1));
    }

    private View createTile(AppEntry app) {
        LinearLayout tile = new LinearLayout((Context)this);
        tile.setOrientation(1);
        tile.setGravity(17);
        tile.setFocusable(true);
        tile.setClickable(true);
        tile.setPadding(this.dp(8.0f), this.dp(10.0f), this.dp(8.0f), this.dp(8.0f));
        tile.setBackground((Drawable)this.round(-2144849605, this.dp(18.0f)));
        ImageView icon = new ImageView((Context)this);
        icon.setImageDrawable(app.icon);
        LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(this.dp(70.0f), this.dp(70.0f));
        ip.bottomMargin = this.dp(7.0f);
        tile.addView((View)icon, (ViewGroup.LayoutParams)ip);
        TextView name = this.label(app.label, 15, -657415, true);
        name.setGravity(17);
        name.setMaxLines(2);
        tile.addView((View)name);
        tile.setOnFocusChangeListener((v, f) -> {
            if (f) {
                this.selected = app;
                tile.setBackground((Drawable)this.round(-1724099998, this.dp(18.0f)));
            } else {
                tile.setBackground((Drawable)this.round(-2144849605, this.dp(18.0f)));
            }
        });
        tile.setOnClickListener(v -> this.launch(app));
        tile.setOnLongClickListener(v -> {
            this.togglePin(app);
            return true;
        });
        return tile;
    }

    private Set<String> getPins() {
        return new HashSet<String>(this.getPreferences(0).getStringSet("pins", new HashSet()));
    }

    private void togglePin(AppEntry app) {
        Set<String> pins = this.getPins();
        if (pins.contains(app.pkg)) {
            pins.remove(app.pkg);
        } else {
            if (pins.size() >= 5) {
                Toast.makeText((Context)this, (CharSequence)this.t("Pin limit reached"), (int)0).show();
                return;
            }
            pins.add(app.pkg);
        }
        this.getPreferences(0).edit().putStringSet("pins", pins).apply();
        this.showPage(this.page);
    }

    private void showAddAppDialog() {
        ArrayList<AppEntry> available = new ArrayList<AppEntry>();
        Set<String> pins = this.getPins();
        for (AppEntry a : this.apps) {
            if (pins.contains(a.pkg)) continue;
            available.add(a);
        }
        if (available.isEmpty()) {
            Toast.makeText((Context)this, (CharSequence)this.t("Pin limit reached"), (int)0).show();
            return;
        }
        CharSequence[] names = new String[available.size()];
        for (int i = 0; i < available.size(); ++i) {
            names[i] = ((AppEntry)available.get((int)i)).label;
        }
        new AlertDialog.Builder((Context)this).setTitle((CharSequence)this.t("Choose an app to pin")).setItems(names, (d, which) -> this.togglePin((AppEntry)available.get(which))).setNegativeButton((CharSequence)this.t("Cancel"), null).show();
    }

    private void showSettings() {
        ScrollView scroll = new ScrollView((Context)this);
        LinearLayout panel = new LinearLayout((Context)this);
        panel.setOrientation(1);
        panel.setPadding(this.dp(20.0f), this.dp(16.0f), this.dp(20.0f), this.dp(20.0f));
        panel.setBackground((Drawable)this.round(-2144849605, this.dp(20.0f)));
        scroll.addView((View)panel);
        this.content.addView((View)scroll, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(-1, -1));
        panel.addView((View)this.label(this.t("Launcher settings"), 19, -657415, true));
        Button scale = this.button(this.t("UI scale") + ": " + this.uiScale + "%");
        scale.setOnClickListener(v -> {
            this.uiScale += 10;
            if (this.uiScale > 130) {
                this.uiScale = 80;
            }
            this.getPreferences(0).edit().putInt("ui_scale", this.uiScale).apply();
            this.buildShell();
            this.showPage("settings");
        });
        panel.addView((View)scale, (ViewGroup.LayoutParams)this.row());
        Button wp = this.button(this.t("Built-in wallpapers"));
        wp.setOnClickListener(v -> { this.wallpaperReturnPage="settings"; this.showPage("wallpapers"); });
        panel.addView((View)wp, (ViewGroup.LayoutParams)this.row());
        Button remove = this.button(this.t("Remove wallpaper"));
        remove.setOnClickListener(v -> {
            this.getPreferences(0).edit().remove("wallpaper").apply();
            this.applyWallpaper();
        });
        panel.addView((View)remove, (ViewGroup.LayoutParams)this.row());
        Button lang = this.button(this.languageLabel());
        lang.setOnClickListener(v -> this.chooseLanguage());
        panel.addView((View)lang, (ViewGroup.LayoutParams)this.row());
        Button defaultLauncher = this.button(this.t("Set as default launcher"));
        defaultLauncher.setOnClickListener(v -> this.openSystemSettings("android.settings.HOME_SETTINGS"));
        panel.addView((View)defaultLauncher, (ViewGroup.LayoutParams)this.row());
        Button system = this.button(this.t("Open Android settings"));
        system.setOnClickListener(v -> this.openSystemSettings("android.settings.SETTINGS"));
        panel.addView((View)system, (ViewGroup.LayoutParams)this.row());
        Button logs = this.button(this.t("Logs"));
        logs.setOnClickListener(v -> this.showPage("logs"));
        panel.addView((View)logs, (ViewGroup.LayoutParams)this.row());
        Button faq = this.button(this.t("FAQ"));
        faq.setOnClickListener(v -> {this.faqReturnPage="settings";this.showPage("faq");});
        panel.addView((View)faq, (ViewGroup.LayoutParams)this.row());
        Button refresh = this.button(this.t("Refresh app list"));
        refresh.setOnClickListener(v -> {
            this.loadApps();
            this.showPage("pinned");
        });
        panel.addView((View)refresh, (ViewGroup.LayoutParams)this.row());
    }

    private LinearLayout.LayoutParams row() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, this.dp(56.0f));
        p.topMargin = this.dp(8.0f);
        return p;
    }

    private void pickWallpaper() {
        try {
            Intent i = new Intent("android.intent.action.OPEN_DOCUMENT");
            i.addCategory("android.intent.category.OPENABLE");
            i.setType("image/*");
            i.addFlags(65);
            this.startActivityForResult(i, 202);
        }
        catch (Exception e) {
            this.selectionHint.setText((CharSequence)this.t("Could not open wallpaper"));
        }
    }

    protected void onActivityResult(int code, int result, Intent data) {
        super.onActivityResult(code, result, data);
        if (code == 202 && result == -1 && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                this.getContentResolver().takePersistableUriPermission(uri, data.getFlags() & 1);
                this.getPreferences(0).edit().putString("wallpaper", uri.toString()).apply();
                this.applyWallpaper();
                this.selectionHint.setText((CharSequence)this.t("Wallpaper updated"));
            }
            catch (Exception e) {
                this.selectionHint.setText((CharSequence)this.t("Could not open wallpaper"));
            }
        }
    }

    private void applyWallpaper() {
        if (this.wallpaper == null) {
            return;
        }
        String saved = this.getPreferences(0).getString("wallpaper", null);
        if (saved == null) {
            this.wallpaper.setImageDrawable(null);
            return;
        }
        try {
            BitmapFactory.Options b = new BitmapFactory.Options();
            b.inJustDecodeBounds = true;
            InputStream in = openWallpaperStream(saved);
            BitmapFactory.decodeStream((InputStream)in, null, (BitmapFactory.Options)b);
            if (in != null) {
                in.close();
            }
            int max = Math.max(this.getResources().getDisplayMetrics().widthPixels, this.getResources().getDisplayMetrics().heightPixels);
            int sample = 1;
            while (b.outWidth / sample > max * 2 || b.outHeight / sample > max * 2) {
                sample *= 2;
            }
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inSampleSize = sample;
            in = openWallpaperStream(saved);
            Bitmap decoded = BitmapFactory.decodeStream((InputStream)in, null, (BitmapFactory.Options)o);
            if (in != null) {
                in.close();
            }
            this.wallpaper.setImageBitmap(this.blurWallpaper(decoded));
        }
        catch (Exception e) {
            this.wallpaper.setImageDrawable(null);
        }
    }

    private InputStream openWallpaperStream(String value) throws Exception {
        if(value.startsWith("asset:"))return this.getAssets().open("wallpapers/"+value.substring(6));
        return this.getContentResolver().openInputStream(Uri.parse(value));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Bitmap blurWallpaper(Bitmap source) {
        if (source == null) {
            return null;
        }
        RenderScript rs = null;
        Allocation input = null;
        Allocation output = null;
        try {
            rs = RenderScript.create((Context)this.getApplicationContext());
            input = Allocation.createFromBitmap((RenderScript)rs, (Bitmap)source);
            Bitmap blurred = Bitmap.createBitmap((int)source.getWidth(), (int)source.getHeight(), (Bitmap.Config)Bitmap.Config.ARGB_8888);
            output = Allocation.createFromBitmap((RenderScript)rs, (Bitmap)blurred);
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create((RenderScript)rs, (Element)Element.U8_4((RenderScript)rs));
            blur.setRadius(18.0f);
            blur.setInput(input);
            blur.forEach(output);
            output.copyTo(blurred);
            blur.destroy();
            source.recycle();
            Bitmap bitmap = blurred;
            return bitmap;
        }
        catch (Exception ignored) {
            Bitmap bitmap = source;
            return bitmap;
        }
        finally {
            if (input != null) {
                input.destroy();
            }
            if (output != null) {
                output.destroy();
            }
            if (rs != null) {
                rs.destroy();
            }
        }
    }

    private void loadApps() {
        this.apps.clear();
        LinkedHashMap<String, AppEntry> found = new LinkedHashMap<String, AppEntry>();
        Intent i = new Intent("android.intent.action.MAIN", null);
        i.addCategory("android.intent.category.LAUNCHER");
        for (ResolveInfo r : this.getPackageManager().queryIntentActivities(i, 0)) {
            this.addEntry(found, r);
        }
        Intent tv = new Intent("android.intent.action.MAIN", null);
        tv.addCategory("android.intent.category.LEANBACK_LAUNCHER");
        for (ResolveInfo r : this.getPackageManager().queryIntentActivities(tv, 0)) {
            this.addEntry(found, r);
        }
        this.apps.addAll(found.values());
        this.sortPinnedFirst();
    }

    private void sortPinnedFirst() {
        HashSet pins = new HashSet(this.getPreferences(0).getStringSet("pins", new HashSet()));
        Collections.sort(this.apps, (a, b) -> {
            boolean bp;
            boolean ap = pins.contains(a.pkg);
            if (ap != (bp = pins.contains(b.pkg))) {
                return ap ? -1 : 1;
            }
            return a.label.compareToIgnoreCase(b.label);
        });
    }

    private void addEntry(LinkedHashMap<String, AppEntry> found, ResolveInfo r) {
        String pkg = r.activityInfo.packageName;
        if (pkg.equals(this.getPackageName()) || found.containsKey(pkg)) {
            return;
        }
        String label = String.valueOf(r.loadLabel(this.getPackageManager()));
        String hidden = label.trim().toLowerCase(Locale.ROOT);
        if (hidden.equals("webview browser tester") || hidden.equals("media center") || hidden.equals("miracast") || hidden.equals("movieplayer") || hidden.equals("netflix") || hidden.equals("pppoe") || hidden.equals("update&backup") || hidden.equals("appinstaller")) {
            return;
        }
        found.put(pkg, new AppEntry(label, pkg, r.loadIcon(this.getPackageManager())));
    }

    private void launch(AppEntry a) {
        try {
            Intent i = this.getPackageManager().getLeanbackLaunchIntentForPackage(a.pkg);
            if (i == null) {
                i = this.getPackageManager().getLaunchIntentForPackage(a.pkg);
            }
            if (i != null) {
                this.startActivity(i);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private void appendSettingsLog(String message) {
        String old = this.getPreferences(0).getString("settings_logs", "");
        String line = new SimpleDateFormat("HH:mm:ss", Locale.ROOT).format(System.currentTimeMillis()) + "  " + message + "\n";
        String next = line + old;
        if (next.length() > 6000) {
            next = next.substring(0, 6000);
        }
        this.getPreferences(0).edit().putString("settings_logs", next).apply();
    }

    private void showLogs() {
        LinearLayout page = new LinearLayout((Context)this);
        page.setOrientation(1);
        page.setPadding(this.dp(18.0f), this.dp(12.0f), this.dp(18.0f), this.dp(12.0f));
        this.content.addView((View)page, (ViewGroup.LayoutParams)new LinearLayout.LayoutParams(-1, -1));
        Button scan = this.button(this.t("Scan settings packages"));
        scan.setOnClickListener(v -> {
            this.scanSettingsPackages();
            this.showPage("logs");
        });
        page.addView((View)scan, (ViewGroup.LayoutParams)this.row());
        Button clear = this.button(this.t("Clear logs"));
        clear.setOnClickListener(v -> {
            this.getPreferences(0).edit().remove("settings_logs").apply();
            this.showPage("logs");
        });
        page.addView((View)clear, (ViewGroup.LayoutParams)this.row());
        ScrollView scroll = new ScrollView((Context)this);
        TextView output = this.label(this.getPreferences(0).getString("settings_logs", this.t("No log entries yet")), 13, -657415, false);
        output.setTextIsSelectable(true);
        output.setPadding(this.dp(8.0f), this.dp(12.0f), this.dp(8.0f), this.dp(12.0f));
        scroll.addView((View)output);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1, 0, 1.0f);
        sp.topMargin = this.dp(10.0f);
        page.addView((View)scroll, (ViewGroup.LayoutParams)sp);
    }

    private void scanSettingsPackages() {
        ArrayList<String> matches = new ArrayList<String>();
        try {
            for (ApplicationInfo app : this.getPackageManager().getInstalledApplications(0)) {
                String pkg = app.packageName;
                if (!pkg.toLowerCase(Locale.ROOT).contains("settings")) continue;
                matches.add(pkg);
            }
        }
        catch (Exception e) {
            this.appendSettingsLog("Package scan failed: " + e.getClass().getSimpleName());
        }
        Collections.sort(matches);
        this.appendSettingsLog("Installed package names containing 'settings': " + (matches.isEmpty() ? "none found" : ((Object)matches).toString()));
        Toast.makeText((Context)this, (CharSequence)(matches.isEmpty() ? "No package names containing settings found" : matches.size() + " settings package(s) added to Logs"), (int)1).show();
    }

    private void openSystemSettings(String action) {
        String[] tvPackages;
        this.appendSettingsLog("Requested " + action);
        for (String pkg : tvPackages = new String[]{"com.android.tv.settings", "com.google.android.tv.settings"}) {
            try {
                Intent targeted = new Intent(action);
                targeted.setPackage(pkg);
                if (targeted.resolveActivity(this.getPackageManager()) != null) {
                    this.startActivity(targeted);
                    this.appendSettingsLog("Opened TV settings action in " + pkg);
                    return;
                }
                this.appendSettingsLog("TV package did not handle action: " + pkg);
            }
            catch (Exception e) {
                this.appendSettingsLog("TV settings action failed for " + pkg + ": " + e.getClass().getSimpleName());
            }
        }
        for (String pkg : tvPackages) {
            try {
                Intent tv = this.getPackageManager().getLeanbackLaunchIntentForPackage(pkg);
                if (tv == null) {
                    tv = this.getPackageManager().getLaunchIntentForPackage(pkg);
                }
                if (tv != null) {
                    this.startActivity(tv);
                    this.appendSettingsLog("Opened TV settings app: " + pkg);
                    return;
                }
                this.appendSettingsLog("TV settings app not installed: " + pkg);
            }
            catch (Exception e) {
                this.appendSettingsLog("Could not open TV settings app " + pkg + ": " + e.getClass().getSimpleName());
            }
        }
        ArrayList<String> found = new ArrayList<String>();
        try {
            Intent leanback = new Intent("android.intent.action.MAIN");
            leanback.addCategory("android.intent.category.LEANBACK_LAUNCHER");
            for (ResolveInfo r : this.getPackageManager().queryIntentActivities(leanback, 0)) {
                String pkg = r.activityInfo.packageName.toLowerCase(Locale.ROOT);
                String label = String.valueOf(r.loadLabel(this.getPackageManager())).toLowerCase(Locale.ROOT);
                if (!label.contains("settings") || !pkg.contains("tv") && !pkg.contains("leanback")) continue;
                found.add(r.activityInfo.packageName);
                Intent tv = this.getPackageManager().getLeanbackLaunchIntentForPackage(r.activityInfo.packageName);
                if (tv == null) continue;
                this.startActivity(tv);
                this.appendSettingsLog("Opened detected TV settings app: " + r.activityInfo.packageName);
                return;
            }
        }
        catch (Exception e) {
            this.appendSettingsLog("TV settings app scan failed: " + e.getClass().getSimpleName());
        }
        this.appendSettingsLog("No TV-style settings app found. Candidates: " + (found.isEmpty() ? "none" : ((Object)found).toString()));
        Toast.makeText((Context)this, (CharSequence)"Whoops! Failed to find Android TV style settings! Opening standard settings...", (int)1).show();
        try {
            this.startActivity(new Intent(action));
            this.appendSettingsLog("Opened standard settings for " + action);
        }
        catch (Exception e) {
            try {
                this.startActivity(new Intent("android.settings.SETTINGS"));
                this.appendSettingsLog("Opened standard Android settings fallback");
            }
            catch (Exception failure) {
                this.appendSettingsLog("Standard settings launch failed: " + failure.getClass().getSimpleName());
            }
        }
    }

    private void updateConnectionIcons() {
        if (this.wifiIcon == null) {
            return;
        }
        int status = 0;
        try {
            boolean connected;
            ConnectivityManager cm = (ConnectivityManager)this.getSystemService("connectivity");
            NetworkInfo w = cm.getNetworkInfo(1);
            NetworkInfo active = cm.getActiveNetworkInfo();
            boolean bl = connected = w != null && w.isConnected();
            if (connected) {
                status = 1;
                boolean validated = false;
                if (active != null && active.isConnected() && active.getType() == 1 && Build.VERSION.SDK_INT >= 23) {
                    Network network = cm.getActiveNetwork();
                    NetworkCapabilities caps = network == null ? null : cm.getNetworkCapabilities(network);
                    boolean bl2 = validated = caps != null && caps.hasCapability(16);
                }
                if (validated) {
                    status = 2;
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.wifiIcon.setStatus(status);
    }

    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getAction() == 0 && e.getKeyCode() == 3) {
            this.showPage("pinned");
            return true;
        }
        if (e.getAction() == 0 && "apps".equals(this.page) && this.appGrid != null && (e.getKeyCode() == 20 || e.getKeyCode() == 19)) {
            View focused = this.getCurrentFocus();
            int position = focused == null ? -1 : this.appGrid.getPositionForView(focused);
            int step = this.appGrid.getNumColumns();
            if (position >= 0) {
                if (e.getKeyCode() == 20 && position < this.apps.size() - 1) {
                    this.appGrid.smoothScrollToPosition(Math.min(this.apps.size() - 1, position + step));
                } else if (e.getKeyCode() == 19) {
                    this.appGrid.smoothScrollToPosition(Math.max(0, position - step));
                }
            }
        }
        return super.dispatchKeyEvent(e);
    }

    public void onBackPressed() {
        if (this.page.equals("setup")) {
            if(setupStep>0){setupStep--;this.showPage("setup");}
        } else if(this.page.equals("wallpapers")) {
            this.showPage(this.wallpaperReturnPage);
        } else if(this.page.equals("faq")) {
            this.showPage(this.faqReturnPage);
        } else if (this.page.equals("logs")) {
            this.showPage("settings");
        } else if (!this.page.equals("pinned")) {
            this.showPage("pinned");
        } else {
            super.onBackPressed();
        }
    }

    protected void onDestroy() {
        this.handler.removeCallbacks(this.clockTicker);
        super.onDestroy();
    }

    private static final class AppEntry {
        final String label;
        final String pkg;
        final Drawable icon;

        AppEntry(String l, String p, Drawable i) {
            this.label = l;
            this.pkg = p;
            this.icon = i;
        }
    }

    private class StatusIconView
    extends FrameLayout {
        private int status;
        private final IconDrawing icon;

        StatusIconView(Context c) {
            super(c);
            this.status = 0;
            this.setFocusable(true);
            this.setClickable(true);
            this.setContentDescription("Wi-Fi settings and connection status");
            this.setBackground((Drawable)MainActivity.this.round(-2144849605, MainActivity.this.dp(24.0f)));
            this.icon = new IconDrawing(c, this);
            this.addView(this.icon, (ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-1, -1));
            this.setOnFocusChangeListener((v, f) -> this.setBackground((Drawable)MainActivity.this.round(f ? -1724099998 : -2144849605, MainActivity.this.dp(24.0f))));
            this.setOnClickListener(v -> MainActivity.this.openSystemSettings("android.settings.WIFI_SETTINGS"));
        }

        void setStatus(int value) {
            if (this.status != value) {
                this.status = value;
                this.icon.invalidate();
            }
        }
    }

    private class AppGridIcon
    extends View {
        private final Paint p;

        AppGridIcon(Context c) {
            super(c);
            this.p = new Paint(3);
        }

        protected void onDraw(Canvas c) {
            super.onDraw(c);
            this.p.setColor(-8857652);
            this.p.setStyle(Paint.Style.FILL);
            float size = MainActivity.this.dp(14.0f);
            float gap = MainActivity.this.dp(5.0f);
            float x = ((float)this.getWidth() - (size * 2.0f + gap)) / 2.0f;
            float y = ((float)this.getHeight() - (size * 2.0f + gap)) / 2.0f;
            c.drawRoundRect(x, y, x + size, y + size, (float)MainActivity.this.dp(3.0f), (float)MainActivity.this.dp(3.0f), this.p);
            c.drawRoundRect(x + size + gap, y, x + size * 2.0f + gap, y + size, (float)MainActivity.this.dp(3.0f), (float)MainActivity.this.dp(3.0f), this.p);
            c.drawRoundRect(x, y + size + gap, x + size, y + size * 2.0f + gap, (float)MainActivity.this.dp(3.0f), (float)MainActivity.this.dp(3.0f), this.p);
            c.drawRoundRect(x + size + gap, y + size + gap, x + size * 2.0f + gap, y + size * 2.0f + gap, (float)MainActivity.this.dp(3.0f), (float)MainActivity.this.dp(3.0f), this.p);
        }
    }

    private final class AppAdapter
    extends BaseAdapter {
        private AppAdapter() {
        }

        public int getCount() {
            return MainActivity.this.apps.size();
        }

        public Object getItem(int p) {
            return MainActivity.this.apps.get(p);
        }

        public long getItemId(int p) {
            return p;
        }

        public View getView(int p, View c, ViewGroup parent) {
            View v = MainActivity.this.createTile((AppEntry)MainActivity.this.apps.get(p));
            v.setLayoutParams((ViewGroup.LayoutParams)new AbsListView.LayoutParams(-1, MainActivity.this.dp(170.0f)));
            return v;
        }
    }

    private class IconDrawing
    extends View {
        private final Paint p;
        private final StatusIconView parent;

        IconDrawing(Context c, StatusIconView owner) {
            super(c);
            this.p = new Paint(3);
            this.parent = owner;
        }

        protected void onDraw(Canvas c) {
            super.onDraw(c);
            this.p.setColor(this.parent.status == 2 ? -9248846 : (this.parent.status == 1 ? -11930 : -30080));
            this.p.setStrokeWidth((float)MainActivity.this.dp(2.4f));
            this.p.setStyle(Paint.Style.STROKE);
            this.p.setStrokeCap(Paint.Cap.ROUND);
            float x = (float)this.getWidth() / 2.0f;
            float y = (float)this.getHeight() / 2.0f;
            c.drawArc(x - (float)MainActivity.this.dp(12.0f), y - (float)MainActivity.this.dp(10.0f), x + (float)MainActivity.this.dp(12.0f), y + (float)MainActivity.this.dp(14.0f), 215.0f, 110.0f, false, this.p);
            c.drawArc(x - (float)MainActivity.this.dp(7.0f), y - (float)MainActivity.this.dp(5.0f), x + (float)MainActivity.this.dp(7.0f), y + (float)MainActivity.this.dp(10.0f), 220.0f, 100.0f, false, this.p);
            this.p.setStyle(Paint.Style.FILL);
            c.drawCircle(x, y + (float)MainActivity.this.dp(8.0f), (float)MainActivity.this.dp(2.2f), this.p);
        }
    }
}
