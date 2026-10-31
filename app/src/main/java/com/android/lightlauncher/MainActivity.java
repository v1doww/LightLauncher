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
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.wifi.WifiManager;
import android.text.InputType;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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
    private static final int[] accentColor_PALETTE = {0xFF78D7CC, 0xFF70B8FF, 0xFFB69CFF, 0xFFFFC86A, 0xFFFF8FAB, 0xFF91E7A2};
    private static final String[] accentColor_NAMES = {"Teal", "Blue", "Violet", "Amber", "Rose", "Green"};
    private int accentColor = accentColor_PALETTE[0];
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
    private View wallpaperShade;
    private TextView weatherLine;
    private TextView weatherIcon;
    private TextView aodClock;
    private TextView aodDate;
    private ImageView aodImage;
    private FrameLayout aodOverlay;
    private String settingsSection = "general";
    private String languageReturnPage = "settings";
    private long lastWeatherFetch;
    private int weatherRequestId;
    private boolean packageReceiverRegistered;
    private BroadcastReceiver packageReceiver;
    private boolean aodPreviewActive;
    private int wallpaperBlur = 18;
    private int aodDimPercent = 15;
    private int aodTimeoutMinutes = 5;
    private int aodSlideSeconds = 30;
    private int aodWallpaperIndex = -1;
    private int clockSize = 48;
    private String[] aodWallpaperFiles = {"alpine_blossom_pass.jpeg", "violet_peak_at_dusk.jpeg", "lakeside_mountain_cabin.jpeg", "golden_hour_tree.webp", "balloon_over_misty_lake.webp", "starfall_reflections.webp", "turquoise_alpine_shore.jpeg", "mirror_lake_peaks.jpeg", "emerald_valley_waters.jpeg"};
    private int wifiLevel = 0;
    private Runnable pendingScaleRebuild;
    private Runnable pendingWallpaperApply;
    private final Runnable idleTimer = () -> enterAod();
    private final Runnable aodSlideshow = () -> advanceAodWallpaper();
    private static final int SCALE_MIN = 70;
    private static final int SCALE_MAX = 150;
    private AppEntry selected;
    private String page = "apps";
    private boolean ukrainian = true;
    private boolean arabic = false;
    private String languageCode = "uk";
    private String wallpaperReturnPage = "settings";
    private String faqReturnPage = "settings";
    private int setupStep = 0;
    private static final String[] LANG_CODES = {"en", "uk", "es", "fr", "de", "pl", "it", "tr", "ar", "ru", "pt"};
    private static final String[] LANG_NAMES = {"English", "Українська", "Español", "Français", "Deutsch", "Polski", "Italiano", "Türkçe", "العربية", "Русский", "Português"};
    private boolean animations = true;
    private int uiScale = 100;
    private final Runnable clockTicker = new Runnable(){

        @Override
        public void run() {
            MainActivity.this.updateClockViews();
            MainActivity.this.updateGreeting();
            long weatherInterval = MainActivity.this.getPreferences(0).getInt("weather_update_minutes", 15) * 60L * 1000L;
            if (MainActivity.this.weatherLine != null && System.currentTimeMillis() - MainActivity.this.lastWeatherFetch > weatherInterval) MainActivity.this.fetchWeather(false);
            if (MainActivity.this.aodClock != null) MainActivity.this.aodClock.setText(MainActivity.this.formattedTime());
            if (MainActivity.this.aodDate != null) MainActivity.this.aodDate.setText(MainActivity.this.formattedDate());
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
        this.accentColor = this.getPreferences(0).getInt("accent_color", accentColor_PALETTE[0]);
        if (!isAccentPaletteColor(this.accentColor)) this.accentColor = accentColor_PALETTE[0];
        this.uiScale = this.getPreferences(0).getInt("ui_scale", 100);
        this.wallpaperBlur = this.getPreferences(0).getInt("wallpaper_blur", 18);
        this.aodDimPercent = this.getPreferences(0).getInt("aod_dim", 15);
        this.aodTimeoutMinutes = this.getPreferences(0).getInt("aod_timeout_minutes", 5);
        this.aodSlideSeconds = this.getPreferences(0).getInt("aod_slide_seconds", 30);
        this.clockSize = this.getPreferences(0).getInt("clock_size", this.isPortrait() ? 38 : 48);
        this.loadApps();
        this.buildShell();
        if (this.getPreferences(0).getBoolean("setup_complete", false)) this.showPage("pinned");
        else this.showPage("setup");
        this.handler.post(this.clockTicker);
        this.fetchWeather(false);
        this.resetIdleTimer();
    }

    @Override protected void onResume() {
        super.onResume();
        if (this.packageReceiver == null) {
            this.packageReceiver = new BroadcastReceiver() {
                @Override public void onReceive(Context context, Intent intent) {
                    MainActivity.this.handler.postDelayed(() -> {
                        MainActivity.this.loadApps();
                        if (MainActivity.this.page.equals("pinned") || MainActivity.this.page.equals("apps") || MainActivity.this.page.equals("addapps")) MainActivity.this.showPage(MainActivity.this.page);
                    }, 500L);
                }
            };
        }
        if (!this.packageReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
            filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            filter.addDataScheme("package");
            registerReceiver(this.packageReceiver, filter);
            this.packageReceiverRegistered = true;
        }
        this.resetIdleTimer();
    }

    @Override protected void onPause() {
        this.handler.removeCallbacks(this.idleTimer);
        if (this.packageReceiverRegistered) {
            try { unregisterReceiver(this.packageReceiver); } catch (Exception ignored) {}
            this.packageReceiverRegistered = false;
        }
        super.onPause();
    }

    private String s(String ua, String en) {
        if ("ar".equals(this.languageCode)) return this.arabicText(en);
        if ("ru".equals(this.languageCode) || "pt".equals(this.languageCode)) return this.additionalTranslation(en);
        if (!"uk".equals(this.languageCode)) return this.localized(en);
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
            case "General": return "\u0417\u0430\u0433\u0430\u043b\u044c\u043d\u0456";
            case "Appearance": return "\u0412\u0438\u0433\u043b\u044f\u0434";
            case "Clock": return "\u0413\u043e\u0434\u0438\u043d\u043d\u0438\u043a";
            case "Weather": return "\u041f\u043e\u0433\u043e\u0434\u0430";
            case "Info": return "\u0406\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0456\u044f";
            case "App version": return "\u0412\u0435\u0440\u0441\u0456\u044f \u0437\u0430\u0441\u0442\u043e\u0441\u0443\u043d\u043a\u0443";
            case "Build date": return "\u0414\u0430\u0442\u0430 \u0437\u0431\u0438\u0440\u043a\u0438";
            case "Accent color": return "\u0410\u043a\u0446\u0435\u043d\u0442\u043d\u0438\u0439 \u043a\u043e\u043b\u0456\u0440";
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

    private String additionalTranslation(String key) {
        if ("ru".equals(this.languageCode)) {
            switch (key) {
                case "Home": return "Главная"; case "All apps": return "Все приложения"; case "Pinned apps": return "Закреплённые приложения"; case "Settings": return "Настройки";
                case "General": return "Общие"; case "Appearance": return "Внешний вид"; case "Clock": return "Часы"; case "Weather": return "Погода"; case "Info": return "Информация";
                case "Set as default launcher": return "Выбрать лаунчер по умолчанию"; case "Add app": return "Добавить приложение"; case "Choose an app to pin": return "Выберите приложение";
                case "No pinned apps": return "Нет закреплённых приложений"; case "Pin limit reached": return "Достигнут лимит закреплений"; case "Clean memory": return "Очистить память";
                case "Choose wallpaper": return "Выбрать обои"; case "Remove wallpaper": return "Удалить обои"; case "Launcher settings": return "Настройки лаунчера";
                case "Language": return "Язык"; case "Refresh app list": return "Обновить список приложений"; case "Open Android settings": return "Открыть настройки Android";
                case "Logs": return "Журнал"; case "FAQ": return "Справка"; case "UI scale": return "Масштаб интерфейса"; case "Animations": return "Анимация";
                case "On": return "Вкл."; case "Off": return "Выкл."; case "Home wallpaper blur": return "Размытие обоев"; case "Home wallpaper dim": return "Затемнение обоев";
                case "Built-in wallpapers": return "Встроенные обои"; case "Find online wallpapers": return "Найти обои в интернете"; case "Choose image from storage": return "Выбрать изображение";
                case "Clock size": return "Размер часов"; case "Time format": return "Формат времени"; case "Clock color": return "Цвет часов"; case "Preview always-on display": return "Предпросмотр экрана ожидания";
                case "Always-on display after": return "Запустить экран ожидания через"; case "Slideshow interval": return "Интервал слайд-шоу"; case "Always-on display dim": return "Затемнение экрана ожидания";
                case "City": return "Город"; case "OpenWeather API key": return "Ключ OpenWeather API"; case "Save weather settings": return "Сохранить настройки погоды";
                case "Weather update interval": return "Интервал обновления погоды"; case "Temperature unit": return "Единицы температуры"; case "Refresh weather now": return "Обновить погоду"; case "Clear weather setup": return "Сбросить погоду";
                case "Good morning": return "Доброе утро"; case "Good afternoon": return "Добрый день"; case "Good evening": return "Добрый вечер";
                case "faq_q1": return "Как закрепить приложение?"; case "faq_a1": return "Удерживайте приложение в разделе «Все приложения». Можно закрепить до пяти.";
                case "faq_q2": return "Как изменить обои?"; case "faq_a2": return "Откройте настройки обоев и выберите встроенное изображение, файл или вариант из интернета.";
                case "faq_q3": return "Как выбрать LightLauncher главным экраном?"; case "faq_a3": return "Выберите LightLauncher в настройках приложений главного экрана Android.";
                case "faq_q4": return "Что означают цвета Wi-Fi?"; case "faq_a4": return "Зелёный — интернет доступен, жёлтый — соединение не проверено, красный — нет подключения.";
                case "faq_q5": return "Что делает очистка памяти?"; case "faq_a5": return "Она просит Android остановить фоновые процессы, если это разрешено.";
                case "faq_q6": return "Как изменить размер интерфейса?"; case "faq_a6": return "Используйте ползунок масштаба в настройках внешнего вида.";
                default: return this.localized(key);
            }
        }
        if ("pt".equals(this.languageCode)) {
            switch (key) {
                case "Home": return "Início"; case "All apps": return "Todos os apps"; case "Pinned apps": return "Apps fixados"; case "Settings": return "Configurações";
                case "General": return "Geral"; case "Appearance": return "Aparência"; case "Clock": return "Relógio"; case "Weather": return "Clima"; case "Info": return "Informações";
                case "Set as default launcher": return "Definir launcher padrão"; case "Add app": return "Adicionar app"; case "Choose an app to pin": return "Escolha um app para fixar";
                case "No pinned apps": return "Nenhum app fixado"; case "Pin limit reached": return "Limite de apps fixados atingido"; case "Clean memory": return "Liberar memória";
                case "Choose wallpaper": return "Escolher papel de parede"; case "Remove wallpaper": return "Remover papel de parede"; case "Launcher settings": return "Configurações do launcher";
                case "Language": return "Idioma"; case "Refresh app list": return "Atualizar lista de apps"; case "Open Android settings": return "Abrir configurações do Android";
                case "Logs": return "Registros"; case "FAQ": return "Ajuda"; case "UI scale": return "Escala da interface"; case "Animations": return "Animações";
                case "On": return "Ativadas"; case "Off": return "Desativadas"; case "Home wallpaper blur": return "Desfoque do papel de parede"; case "Home wallpaper dim": return "Escurecer papel de parede";
                case "Built-in wallpapers": return "Papéis de parede incluídos"; case "Find online wallpapers": return "Buscar papéis de parede online"; case "Choose image from storage": return "Escolher imagem do armazenamento";
                case "Clock size": return "Tamanho do relógio"; case "Time format": return "Formato da hora"; case "Clock color": return "Cor do relógio"; case "Preview always-on display": return "Pré-visualizar tela sempre ativa";
                case "Always-on display after": return "Ativar tela sempre ativa após"; case "Slideshow interval": return "Intervalo da apresentação"; case "Always-on display dim": return "Escurecimento da tela sempre ativa";
                case "City": return "Cidade"; case "OpenWeather API key": return "Chave da API OpenWeather"; case "Save weather settings": return "Salvar configurações do clima";
                case "Weather update interval": return "Intervalo de atualização do clima"; case "Temperature unit": return "Unidade de temperatura"; case "Refresh weather now": return "Atualizar clima agora"; case "Clear weather setup": return "Limpar configuração do clima";
                case "Good morning": return "Bom dia"; case "Good afternoon": return "Boa tarde"; case "Good evening": return "Boa noite";
                case "faq_q1": return "Como fixo um app?"; case "faq_a1": return "Mantenha um app pressionado em Todos os apps. Você pode fixar até cinco.";
                case "faq_q2": return "Como altero o papel de parede?"; case "faq_a2": return "Abra as opções de papel de parede e escolha uma imagem incluída, um arquivo ou uma opção online.";
                case "faq_q3": return "Como defino o LightLauncher como tela inicial?"; case "faq_a3": return "Escolha LightLauncher nas configurações de tela inicial do Android.";
                case "faq_q4": return "O que significam as cores do Wi-Fi?"; case "faq_a4": return "Verde: internet disponível; amarelo: conexão não verificada; vermelho: desconectado.";
                case "faq_q5": return "O que faz Liberar memória?"; case "faq_a5": return "O Android é solicitado a parar processos em segundo plano quando permitido.";
                case "faq_q6": return "Como redimensiono a interface?"; case "faq_a6": return "Use o controle de escala nas configurações de aparência.";
                default: return this.localized(key);
            }
        }
        return this.localized(key);
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
        else if ("ru".equals(languageCode)) tag = "ru-RU";
        else if ("pt".equals(languageCode)) tag = "pt-PT";
        else if ("ja".equals(languageCode)) tag = "ja-JP";
        else if ("zh".equals(languageCode)) tag = "zh-CN";
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
        return index < v.length ? v[index] : v[0];
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

    private String settingsTranslation(String key) {
        String[] values;
        switch (key) {
            case "General": values = new String[]{"General", "\u0417\u0430\u0433\u0430\u043b\u044c\u043d\u0456", "General", "G\u00e9n\u00e9ral", "Allgemein", "Og\u00f3lne", "Generale", "Genel", "\u0639\u0627\u0645", "\u041e\u0431\u0449\u0438\u0435", "Geral"}; break;
            case "Appearance": values = new String[]{"Appearance", "\u0412\u0438\u0433\u043b\u044f\u0434", "Apariencia", "Apparence", "Darstellung", "Wygl\u0105d", "Aspetto", "G\u00f6r\u00fcn\u00fcm", "\u0627\u0644\u0645\u0638\u0647\u0631", "\u0412\u043d\u0435\u0448\u043d\u0438\u0439 \u0432\u0438\u0434", "Apar\u00eancia"}; break;
            case "Clock": values = new String[]{"Clock", "\u0413\u043e\u0434\u0438\u043d\u043d\u0438\u043a", "Reloj", "Horloge", "Uhr", "Zegar", "Orologio", "Saat", "\u0627\u0644\u0633\u0627\u0639\u0629", "\u0427\u0430\u0441\u044b", "Rel\u00f3gio"}; break;
            case "Weather": values = new String[]{"Weather", "\u041f\u043e\u0433\u043e\u0434\u0430", "Tiempo", "M\u00e9t\u00e9o", "Wetter", "Pogoda", "Meteo", "Hava durumu", "\u0627\u0644\u0637\u0642\u0633", "\u041f\u043e\u0433\u043e\u0434\u0430", "Clima"}; break;
            case "Info": values = new String[]{"Info", "\u0406\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0456\u044f", "Info", "Infos", "Info", "Informacje", "Info", "Bilgi", "\u0645\u0639\u0644\u0648\u0645\u0627\u062a", "\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f", "Informa\u00e7\u00f5es"}; break;
            case "Language": values = new String[]{"Language", "\u041c\u043e\u0432\u0430", "Idioma", "Langue", "Sprache", "J\u0119zyk", "Lingua", "Dil", "\u0627\u0644\u0644\u063a\u0629", "\u042f\u0437\u044b\u043a", "Idioma"}; break;
            case "Launcher settings": values = new String[]{"Launcher settings", "\u041d\u0430\u043b\u0430\u0448\u0442\u0443\u0432\u0430\u043d\u043d\u044f \u043b\u0430\u0443\u043d\u0447\u0435\u0440\u0430", "Ajustes del launcher", "Param\u00e8tres du lanceur", "Launcher-Einstellungen", "Ustawienia launchera", "Impostazioni launcher", "Ba\u015flat\u0131c\u0131 ayarlar\u0131", "\u0625\u0639\u062f\u0627\u062f\u0627\u062a \u0627\u0644\u0645\u0634\u063a\u0644", "\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u043b\u0430\u0443\u043d\u0447\u0435\u0440\u0430", "Configura\u00e7\u00f5es do launcher"}; break;
            case "App version": values = new String[]{"App version", "\u0412\u0435\u0440\u0441\u0456\u044f \u0437\u0430\u0441\u0442\u043e\u0441\u0443\u043d\u043a\u0443", "Versi\u00f3n de la app", "Version de l\u2019application", "App-Version", "Wersja aplikacji", "Versione app", "Uygulama s\u00fcr\u00fcm\u00fc", "\u0625\u0635\u062f\u0627\u0631 \u0627\u0644\u062a\u0637\u0628\u064a\u0642", "\u0412\u0435\u0440\u0441\u0438\u044f \u043f\u0440\u0438\u043b\u043e\u0436\u0435\u043d\u0438\u044f", "Vers\u00e3o do app"}; break;
            case "Build date": values = new String[]{"Build date", "\u0414\u0430\u0442\u0430 \u0437\u0431\u0456\u0440\u043a\u0438", "Fecha de compilaci\u00f3n", "Date de compilation", "Build-Datum", "Data kompilacji", "Data build", "Derleme tarihi", "\u062a\u0627\u0631\u064a\u062e \u0627\u0644\u0625\u0635\u062f\u0627\u0631", "\u0414\u0430\u0442\u0430 \u0441\u0431\u043e\u0440\u043a\u0438", "Data da compila\u00e7\u00e3o"}; break;
            case "Accent color": values = new String[]{"Accent color", "\u0410\u043a\u0446\u0435\u043d\u0442\u043d\u0438\u0439 \u043a\u043e\u043b\u0456\u0440", "Color de acento", "Couleur d\u2019accent", "Akzentfarbe", "Kolor akcentu", "Colore in risalto", "Vurgu rengi", "\u0644\u0648\u0646 \u0627\u0644\u062a\u0645\u064a\u064a\u0632", "\u0426\u0432\u0435\u0442 \u0430\u043a\u0446\u0435\u043d\u0442\u0430", "Cor de destaque"}; break;
            case "Teal": values = new String[]{"Teal", "\u0411\u0456\u0440\u044e\u0437\u043e\u0432\u0438\u0439", "Turquesa", "Turquoise", "T\u00fcrkis", "Turkusowy", "Turchese", "Turkuaz", "\u0641\u064a\u0631\u0648\u0632\u064a", "\u0411\u0438\u0440\u044e\u0437\u043e\u0432\u044b\u0439", "Turquesa"}; break;
            case "Blue": values = new String[]{"Blue", "\u0421\u0438\u043d\u0456\u0439", "Azul", "Bleu", "Blau", "Niebieski", "Blu", "Mavi", "\u0623\u0632\u0631\u0642", "\u0421\u0438\u043d\u0438\u0439", "Azul"}; break;
            case "Violet": values = new String[]{"Violet", "\u0424\u0456\u043e\u043b\u0435\u0442\u043e\u0432\u0438\u0439", "Violeta", "Violet", "Violett", "Fioletowy", "Viola", "Mor", "\u0628\u0646\u0641\u0633\u062c\u064a", "\u0424\u0438\u043e\u043b\u0435\u0442\u043e\u0432\u044b\u0439", "Violeta"}; break;
            case "Amber": values = new String[]{"Amber", "\u0411\u0443\u0440\u0448\u0442\u0438\u043d\u043e\u0432\u0438\u0439", "\u00c1mbar", "Ambre", "Bernstein", "Bursztynowy", "Ambra", "Kehribar", "\u0643\u0647\u0631\u0645\u0627\u0646\u064a", "\u042f\u043d\u0442\u0430\u0440\u043d\u044b\u0439", "\u00c2mbar"}; break;
            case "Rose": values = new String[]{"Rose", "\u0420\u043e\u0436\u0435\u0432\u0438\u0439", "Rosa", "Rose", "Rosa", "R\u00f3\u017cowy", "Rosa", "Pembe", "\u0648\u0631\u062f\u064a", "\u0420\u043e\u0437\u043e\u0432\u044b\u0439", "Rosa"}; break;
            case "Green": values = new String[]{"Green", "\u0417\u0435\u043b\u0435\u043d\u0438\u0439", "Verde", "Vert", "Gr\u00fcn", "Zielony", "Verde", "Ye\u015fil", "\u0623\u062e\u0636\u0631", "\u0417\u0435\u043b\u0451\u043d\u044b\u0439", "Verde"}; break;
            case "Set as default launcher": values = new String[]{"Set as default launcher", "\u0412\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u0438 \u043b\u0430\u0443\u043d\u0447\u0435\u0440 \u0437\u0430 \u0437\u0430\u043c\u043e\u0432\u0447\u0443\u0432\u0430\u043d\u043d\u044f\u043c", "Usar como launcher predeterminado", "D\u00e9finir comme lanceur par d\u00e9faut", "Als Standard-Launcher festlegen", "Ustaw jako domy\u015blny launcher", "Imposta launcher predefinito", "Varsay\u0131lan ba\u015flat\u0131c\u0131 yap", "\u062a\u0639\u064a\u064a\u0646 \u0643\u0645\u0634\u063a\u0644 \u0627\u0641\u062a\u0631\u0627\u0636\u064a", "\u0412\u044b\u0431\u0440\u0430\u0442\u044c \u043b\u0430\u0443\u043d\u0447\u0435\u0440 \u043f\u043e \u0443\u043c\u043e\u043b\u0447\u0430\u043d\u0438\u044e", "Definir launcher padr\u00e3o"}; break;
            case "Open Android settings": values = new String[]{"Open Android settings", "\u0412\u0456\u0434\u043a\u0440\u0438\u0442\u0438 \u043d\u0430\u043b\u0430\u0448\u0442\u0443\u0432\u0430\u043d\u043d\u044f Android", "Abrir ajustes de Android", "Ouvrir les param\u00e8tres Android", "Android-Einstellungen \u00f6ffnen", "Otw\u00f3rz ustawienia Androida", "Apri impostazioni Android", "Android ayarlar\u0131n\u0131 a\u00e7", "\u0641\u062a\u062d \u0625\u0639\u062f\u0627\u062f\u0627\u062a Android", "\u041e\u0442\u043a\u0440\u044b\u0442\u044c \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 Android", "Abrir configura\u00e7\u00f5es do Android"}; break;
            case "Animations": values = new String[]{"Animations", "\u0410\u043d\u0456\u043c\u0430\u0446\u0456\u0457", "Animaciones", "Animations", "Animationen", "Animacje", "Animazioni", "Animasyonlar", "\u0627\u0644\u062d\u0631\u0643\u0627\u062a", "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f", "Anima\u00e7\u00f5es"}; break;
            case "On": values = new String[]{"On", "\u0423\u0432\u0456\u043c\u043a\u043d\u0435\u043d\u043e", "Activadas", "Activ\u00e9es", "Ein", "W\u0142\u0105czone", "Attive", "A\u00e7\u0131k", "\u0645\u0641\u0639\u0651\u0644", "\u0412\u043a\u043b.", "Ativadas"}; break;
            case "Off": values = new String[]{"Off", "\u0412\u0438\u043c\u043a\u043d\u0435\u043d\u043e", "Desactivadas", "D\u00e9sactiv\u00e9es", "Aus", "Wy\u0142\u0105czone", "Disattivate", "Kapal\u0131", "\u0645\u062a\u0648\u0642\u0641", "\u0412\u044b\u043a\u043b.", "Desativadas"}; break;
            case "Logs": values = new String[]{"Logs", "\u0416\u0443\u0440\u043d\u0430\u043b", "Registros", "Journaux", "Protokolle", "Dziennik", "Registri", "Kay\u0131tlar", "\u0627\u0644\u0633\u062c\u0644\u0627\u062a", "\u0416\u0443\u0440\u043d\u0430\u043b", "Registros"}; break;
            case "FAQ": values = new String[]{"FAQ", "\u0417\u0430\u043f\u0438\u0442\u0430\u043d\u043d\u044f \u0439 \u0432\u0456\u0434\u043f\u043e\u0432\u0456\u0434\u0456", "Preguntas frecuentes", "FAQ", "H\u00e4ufige Fragen", "FAQ", "Domande frequenti", "SSS", "\u0627\u0644\u0623\u0633\u0626\u0644\u0629 \u0627\u0644\u0634\u0627\u0626\u0639\u0629", "\u0421\u043f\u0440\u0430\u0432\u043a\u0430", "Perguntas frequentes"}; break;
            case "Refresh app list": values = new String[]{"Refresh app list", "\u041e\u043d\u043e\u0432\u0438\u0442\u0438 \u0441\u043f\u0438\u0441\u043e\u043a \u043f\u0440\u043e\u0433\u0440\u0430\u043c", "Actualizar lista de apps", "Actualiser la liste des applis", "App-Liste aktualisieren", "Od\u015bwie\u017c list\u0119 aplikacji", "Aggiorna elenco app", "Uygulama listesini yenile", "\u062a\u062d\u062f\u064a\u062b \u0642\u0627\u0626\u0645\u0629 \u0627\u0644\u062a\u0637\u0628\u064a\u0642\u0627\u062a", "\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c \u0441\u043f\u0438\u0441\u043e\u043a \u043f\u0440\u0438\u043b\u043e\u0436\u0435\u043d\u0438\u0439", "Atualizar lista de apps"}; break;
            case "UI scale": values = new String[]{"UI scale", "\u041c\u0430\u0441\u0448\u0442\u0430\u0431 \u0456\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0443", "Escala de interfaz", "\u00c9chelle de l\u2019interface", "UI-Skalierung", "Skala interfejsu", "Scala interfaccia", "Aray\u00fcz \u00f6l\u00e7e\u011fi", "\u062d\u062c\u0645 \u0627\u0644\u0648\u0627\u062c\u0647\u0629", "\u041c\u0430\u0441\u0448\u0442\u0430\u0431 \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441\u0430", "Escala da interface"}; break;
            case "Home wallpaper blur": values = new String[]{"Home wallpaper blur", "\u0420\u043e\u0437\u043c\u0438\u0442\u0442\u044f \u0448\u043f\u0430\u043b\u0435\u0440 \u0433\u043e\u043b\u043e\u0432\u043d\u043e\u0433\u043e \u0435\u043a\u0440\u0430\u043d\u0430", "Desenfoque del fondo de inicio", "Flou du fond d\u2019\u00e9cran d\u2019accueil", "Unsch\u00e4rfe des Startbildschirm-Hintergrunds", "Rozmycie tapety ekranu g\u0142\u00f3wnego", "Sfocatura sfondo Home", "Ana ekran duvar k\u00e2\u011f\u0131d\u0131 bulan\u0131kl\u0131\u011f\u0131", "\u062a\u0645\u0648\u064a\u0647 \u062e\u0644\u0641\u064a\u0629 \u0627\u0644\u0634\u0627\u0634\u0629 \u0627\u0644\u0631\u0626\u064a\u0633\u064a\u0629", "\u0420\u0430\u0437\u043c\u044b\u0442\u0438\u0435 \u043e\u0431\u043e\u0435\u0432 \u0433\u043b\u0430\u0432\u043d\u043e\u0433\u043e \u044d\u043a\u0440\u0430\u043d\u0430", "Desfoque do papel de parede inicial"}; break;
            case "Home wallpaper dim": values = new String[]{"Home wallpaper dim", "\u0417\u0430\u0442\u0435\u043c\u043d\u0435\u043d\u043d\u044f \u0448\u043f\u0430\u043b\u0435\u0440 \u0433\u043e\u043b\u043e\u0432\u043d\u043e\u0433\u043e \u0435\u043a\u0440\u0430\u043d\u0430", "Oscurecer fondo de inicio", "Assombrir le fond d\u2019\u00e9cran d\u2019accueil", "Startbildschirm-Hintergrund abdunkeln", "Przyciemnienie tapety ekranu g\u0142\u00f3wnego", "Oscura sfondo Home", "Ana ekran duvar k\u00e2\u011f\u0131d\u0131n\u0131 karart", "\u062a\u0639\u062a\u064a\u0645 \u062e\u0644\u0641\u064a\u0629 \u0627\u0644\u0634\u0627\u0634\u0629 \u0627\u0644\u0631\u0626\u064a\u0633\u064a\u0629", "\u0417\u0430\u0442\u0435\u043c\u043d\u0435\u043d\u0438\u0435 \u043e\u0431\u043e\u0435\u0432 \u0433\u043b\u0430\u0432\u043d\u043e\u0433\u043e \u044d\u043a\u0440\u0430\u043d\u0430", "Escurecer papel de parede inicial"}; break;
            case "Built-in wallpapers": values = new String[]{"Built-in wallpapers", "\u0412\u0431\u0443\u0434\u043e\u0432\u0430\u043d\u0456 \u0448\u043f\u0430\u043b\u0435\u0440\u0438", "Fondos incluidos", "Fonds d\u2019\u00e9cran int\u00e9gr\u00e9s", "Integrierte Hintergrundbilder", "Wbudowane tapety", "Sfondi inclusi", "Dahili duvar k\u00e2\u011f\u0131tlar\u0131", "\u062e\u0644\u0641\u064a\u0627\u062a \u0645\u0636\u0645\u0651\u0646\u0629", "\u0412\u0441\u0442\u0440\u043e\u0435\u043d\u043d\u044b\u0435 \u043e\u0431\u043e\u0438", "Pap\u00e9is de parede inclu\u00eddos"}; break;
            case "Find online wallpapers": values = new String[]{"Find online wallpapers", "\u0417\u043d\u0430\u0439\u0442\u0438 \u0448\u043f\u0430\u043b\u0435\u0440\u0438 \u0432 \u0456\u043d\u0442\u0435\u0440\u043d\u0435\u0442\u0456", "Buscar fondos en l\u00ednea", "Rechercher des fonds en ligne", "Hintergrundbilder online suchen", "Znajd\u017a tapety online", "Cerca sfondi online", "\u00c7evrimi\u00e7i duvar k\u00e2\u011f\u0131d\u0131 bul", "\u0627\u0644\u0628\u062d\u062b \u0639\u0646 \u062e\u0644\u0641\u064a\u0627\u062a \u0639\u0628\u0631 \u0627\u0644\u0625\u0646\u062a\u0631\u0646\u062a", "\u041d\u0430\u0439\u0442\u0438 \u043e\u0431\u043e\u0438 \u0432 \u0438\u043d\u0442\u0435\u0440\u043d\u0435\u0442\u0435", "Buscar pap\u00e9is de parede online"}; break;
            case "Choose image from storage": values = new String[]{"Choose image from storage", "\u0412\u0438\u0431\u0440\u0430\u0442\u0438 \u0437\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u043d\u044f \u0437\u0456 \u0441\u0445\u043e\u0432\u0438\u0449\u0430", "Elegir imagen del almacenamiento", "Choisir une image du stockage", "Bild aus Speicher ausw\u00e4hlen", "Wybierz obraz z pami\u0119ci", "Scegli immagine dalla memoria", "Depolamadan g\u00f6rsel se\u00e7", "\u0627\u062e\u062a\u064a\u0627\u0631 \u0635\u0648\u0631\u0629 \u0645\u0646 \u0627\u0644\u062a\u062e\u0632\u064a\u0646", "\u0412\u044b\u0431\u0440\u0430\u0442\u044c \u0438\u0437\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u0438\u0435 \u0438\u0437 \u0445\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0430", "Escolher imagem do armazenamento"}; break;
            case "Remove wallpaper": values = new String[]{"Remove wallpaper", "\u0412\u0438\u0434\u0430\u043b\u0438\u0442\u0438 \u0448\u043f\u0430\u043b\u0435\u0440\u0438", "Quitar fondo", "Supprimer le fond d\u2019\u00e9cran", "Hintergrundbild entfernen", "Usu\u0144 tapet\u0119", "Rimuovi sfondo", "Duvar k\u00e2\u011f\u0131d\u0131n\u0131 kald\u0131r", "\u0625\u0632\u0627\u0644\u0629 \u0627\u0644\u062e\u0644\u0641\u064a\u0629", "\u0423\u0434\u0430\u043b\u0438\u0442\u044c \u043e\u0431\u043e\u0438", "Remover papel de parede"}; break;
            case "Clock size": values = new String[]{"Clock size", "\u0420\u043e\u0437\u043c\u0456\u0440 \u0433\u043e\u0434\u0438\u043d\u043d\u0438\u043a\u0430", "Tama\u00f1o del reloj", "Taille de l\u2019horloge", "Uhrgr\u00f6\u00dfe", "Rozmiar zegara", "Dimensione orologio", "Saat boyutu", "\u062d\u062c\u0645 \u0627\u0644\u0633\u0627\u0639\u0629", "\u0420\u0430\u0437\u043c\u0435\u0440 \u0447\u0430\u0441\u043e\u0432", "Tamanho do rel\u00f3gio"}; break;
            case "Time format": values = new String[]{"Time format", "\u0424\u043e\u0440\u043c\u0430\u0442 \u0447\u0430\u0441\u0443", "Formato de hora", "Format de l\u2019heure", "Zeitformat", "Format czasu", "Formato ora", "Saat bi\u00e7imi", "\u062a\u0646\u0633\u064a\u0642 \u0627\u0644\u0648\u0642\u062a", "\u0424\u043e\u0440\u043c\u0430\u0442 \u0432\u0440\u0435\u043c\u0435\u043d\u0438", "Formato da hora"}; break;
            case "Clock color": values = new String[]{"Clock color", "\u041a\u043e\u043b\u0456\u0440 \u0433\u043e\u0434\u0438\u043d\u043d\u0438\u043a\u0430", "Color del reloj", "Couleur de l\u2019horloge", "Uhrfarbe", "Kolor zegara", "Colore orologio", "Saat rengi", "\u0644\u0648\u0646 \u0627\u0644\u0633\u0627\u0639\u0629", "\u0426\u0432\u0435\u0442 \u0447\u0430\u0441\u043e\u0432", "Cor do rel\u00f3gio"}; break;
            case "Preview always-on display": values = new String[]{"Preview always-on display", "\u041f\u0435\u0440\u0435\u0433\u043b\u044f\u043d\u0443\u0442\u0438 \u0440\u0435\u0436\u0438\u043c \u043f\u043e\u0441\u0442\u0456\u0439\u043d\u043e\u0433\u043e \u0434\u0438\u0441\u043f\u043b\u0435\u044f", "Vista previa de pantalla siempre activa", "Aper\u00e7u de l\u2019affichage permanent", "Always-on-Display-Vorschau", "Podgl\u0105d ekranu zawsze w\u0142\u0105czonego", "Anteprima schermo sempre attivo", "Her zaman a\u00e7\u0131k ekran \u00f6nizlemesi", "\u0645\u0639\u0627\u064a\u0646\u0629 \u0627\u0644\u0634\u0627\u0634\u0629 \u0627\u0644\u062f\u0627\u0626\u0645\u0629", "\u041f\u0440\u0435\u0434\u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440 \u0440\u0435\u0436\u0438\u043c\u0430 \u043e\u0436\u0438\u0434\u0430\u043d\u0438\u044f", "Pr\u00e9-visualizar tela sempre ativa"}; break;
            case "Always-on display after": values = new String[]{"Always-on display after", "\u0423\u0432\u0456\u043c\u043a\u043d\u0443\u0442\u0438 \u0440\u0435\u0436\u0438\u043c \u043e\u0447\u0456\u043a\u0443\u0432\u0430\u043d\u043d\u044f \u0447\u0435\u0440\u0435\u0437", "Activar pantalla siempre activa tras", "Activer l\u2019affichage permanent apr\u00e8s", "Always-on-Display nach", "W\u0142\u0105cz ekran zawsze aktywny po", "Attiva schermo sempre attivo dopo", "Her zaman a\u00e7\u0131k ekran\u0131 \u015fu s\u00fcrede ba\u015flat", "\u062a\u0634\u063a\u064a\u0644 \u0627\u0644\u0634\u0627\u0634\u0629 \u0627\u0644\u062f\u0627\u0626\u0645\u0629 \u0628\u0639\u062f", "\u0417\u0430\u043f\u0443\u0441\u0442\u0438\u0442\u044c \u044d\u043a\u0440\u0430\u043d \u043e\u0436\u0438\u0434\u0430\u043d\u0438\u044f \u0447\u0435\u0440\u0435\u0437", "Ativar tela sempre ativa ap\u00f3s"}; break;
            case "Slideshow interval": values = new String[]{"Slideshow interval", "\u0406\u043d\u0442\u0435\u0440\u0432\u0430\u043b \u0437\u043c\u0456\u043d\u0438 \u0448\u043f\u0430\u043b\u0435\u0440", "Intervalo de presentaci\u00f3n", "Intervalle du diaporama", "Diashow-Intervall", "Czas zmiany tapet", "Intervallo presentazione", "Slayt g\u00f6sterisi aral\u0131\u011f\u0131", "\u0627\u0644\u0641\u0627\u0635\u0644 \u0628\u064a\u0646 \u0627\u0644\u0635\u0648\u0631", "\u0418\u043d\u0442\u0435\u0440\u0432\u0430\u043b \u0441\u043b\u0430\u0439\u0434-\u0448\u043e\u0443", "Intervalo da apresenta\u00e7\u00e3o"}; break;
            case "Always-on display dim": values = new String[]{"Always-on display dim", "\u0417\u0430\u0442\u0435\u043c\u043d\u0435\u043d\u043d\u044f \u0440\u0435\u0436\u0438\u043c\u0443 \u043e\u0447\u0456\u043a\u0443\u0432\u0430\u043d\u043d\u044f", "Oscurecer pantalla siempre activa", "Assombrir l\u2019affichage permanent", "Always-on-Display abdunkeln", "Przyciemnienie ekranu zawsze aktywnego", "Oscura schermo sempre attivo", "Her zaman a\u00e7\u0131k ekran\u0131 karart", "\u062a\u0639\u062a\u064a\u0645 \u0627\u0644\u0634\u0627\u0634\u0629 \u0627\u0644\u062f\u0627\u0626\u0645\u0629", "\u0417\u0430\u0442\u0435\u043c\u043d\u0435\u043d\u0438\u0435 \u044d\u043a\u0440\u0430\u043d\u0430 \u043e\u0436\u0438\u0434\u0430\u043d\u0438\u044f", "Escurecer tela sempre ativa"}; break;
            case "City": values = new String[]{"City", "\u041c\u0456\u0441\u0442\u043e", "Ciudad", "Ville", "Stadt", "Miasto", "Citt\u00e0", "\u015eehir", "\u0627\u0644\u0645\u062f\u064a\u0646\u0629", "\u0413\u043e\u0440\u043e\u0434", "Cidade"}; break;
            case "OpenWeather API key": values = new String[]{"OpenWeather API key", "\u041a\u043b\u044e\u0447 API OpenWeather", "Clave API de OpenWeather", "Cl\u00e9 API OpenWeather", "OpenWeather-API-Schl\u00fcssel", "Klucz API OpenWeather", "Chiave API OpenWeather", "OpenWeather API anahtar\u0131", "\u0645\u0641\u062a\u0627\u062d OpenWeather API", "\u041a\u043b\u044e\u0447 OpenWeather API", "Chave da API OpenWeather"}; break;
            case "Save weather settings": values = new String[]{"Save weather settings", "\u0417\u0431\u0435\u0440\u0435\u0433\u0442\u0438 \u043d\u0430\u043b\u0430\u0448\u0442\u0443\u0432\u0430\u043d\u043d\u044f \u043f\u043e\u0433\u043e\u0434\u0438", "Guardar ajustes del clima", "Enregistrer les param\u00e8tres m\u00e9t\u00e9o", "Wettereinstellungen speichern", "Zapisz ustawienia pogody", "Salva impostazioni meteo", "Hava durumu ayarlar\u0131n\u0131 kaydet", "\u062d\u0641\u0638 \u0625\u0639\u062f\u0627\u062f\u0627\u062a \u0627\u0644\u0637\u0642\u0633", "\u0421\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u043f\u043e\u0433\u043e\u0434\u044b", "Salvar configura\u00e7\u00f5es do clima"}; break;
            case "Weather update interval": values = new String[]{"Weather update interval", "\u0406\u043d\u0442\u0435\u0440\u0432\u0430\u043b \u043e\u043d\u043e\u0432\u043b\u0435\u043d\u043d\u044f \u043f\u043e\u0433\u043e\u0434\u0438", "Intervalo de actualizaci\u00f3n del clima", "Intervalle d\u2019actualisation m\u00e9t\u00e9o", "Wetter-Aktualisierungsintervall", "Cz\u0119stotliwo\u015b\u0107 aktualizacji pogody", "Intervallo aggiornamento meteo", "Hava durumu yenileme aral\u0131\u011f\u0131", "\u0627\u0644\u0641\u0627\u0635\u0644 \u0627\u0644\u0632\u0645\u0646\u064a \u0644\u062a\u062d\u062f\u064a\u062b \u0627\u0644\u0637\u0642\u0633", "\u0418\u043d\u0442\u0435\u0440\u0432\u0430\u043b \u043e\u0431\u043d\u043e\u0432\u043b\u0435\u043d\u0438\u044f \u043f\u043e\u0433\u043e\u0434\u044b", "Intervalo de atualiza\u00e7\u00e3o do clima"}; break;
            case "Temperature unit": values = new String[]{"Temperature unit", "\u041e\u0434\u0438\u043d\u0438\u0446\u0456 \u0442\u0435\u043c\u043f\u0435\u0440\u0430\u0442\u0443\u0440\u0438", "Unidad de temperatura", "Unit\u00e9 de temp\u00e9rature", "Temperatureinheit", "Jednostka temperatury", "Unit\u00e0 temperatura", "S\u0131cakl\u0131k birimi", "\u0648\u062d\u062f\u0629 \u0627\u0644\u062d\u0631\u0627\u0631\u0629", "\u0415\u0434\u0438\u043d\u0438\u0446\u044b \u0442\u0435\u043c\u043f\u0435\u0440\u0430\u0442\u0443\u0440\u044b", "Unidade de temperatura"}; break;
            case "Refresh weather now": values = new String[]{"Refresh weather now", "\u041e\u043d\u043e\u0432\u0438\u0442\u0438 \u043f\u043e\u0433\u043e\u0434\u0443 \u0437\u0430\u0440\u0430\u0437", "Actualizar clima ahora", "Actualiser la m\u00e9t\u00e9o", "Wetter jetzt aktualisieren", "Od\u015bwie\u017c pogod\u0119 teraz", "Aggiorna meteo ora", "Hava durumunu \u015fimdi yenile", "\u062a\u062d\u062f\u064a\u062b \u0627\u0644\u0637\u0642\u0633 \u0627\u0644\u0622\u0646", "\u041e\u0431\u043d\u043e\u0432\u0438\u0442\u044c \u043f\u043e\u0433\u043e\u0434\u0443", "Atualizar clima agora"}; break;
            case "Clear weather setup": values = new String[]{"Clear weather setup", "\u041e\u0447\u0438\u0441\u0442\u0438\u0442\u0438 \u043d\u0430\u043b\u0430\u0448\u0442\u0443\u0432\u0430\u043d\u043d\u044f \u043f\u043e\u0433\u043e\u0434\u0438", "Borrar ajustes del clima", "Effacer la configuration m\u00e9t\u00e9o", "Wettereinrichtung l\u00f6schen", "Wyczy\u015b\u0107 ustawienia pogody", "Cancella impostazioni meteo", "Hava durumu ayarlar\u0131n\u0131 temizle", "\u0645\u0633\u062d \u0625\u0639\u062f\u0627\u062f\u0627\u062a \u0627\u0644\u0637\u0642\u0633", "\u0421\u0431\u0440\u043e\u0441\u0438\u0442\u044c \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438 \u043f\u043e\u0433\u043e\u0434\u044b", "Limpar configura\u00e7\u00e3o do clima"}; break;
            case "12-hour": values = new String[]{"12-hour", "12-\u0433\u043e\u0434\u0438\u043d\u043d\u0438\u0439", "12 horas", "12 heures", "12 Stunden", "12-godzinny", "12 ore", "12 saat", "12 \u0633\u0627\u0639\u0629", "12-\u0447\u0430\u0441\u043e\u0432\u043e\u0439", "12 horas"}; break;
            case "24-hour": values = new String[]{"24-hour", "24-\u0433\u043e\u0434\u0438\u043d\u043d\u0438\u0439", "24 horas", "24 heures", "24 Stunden", "24-godzinny", "24 ore", "24 saat", "24 \u0633\u0627\u0639\u0629", "24-\u0447\u0430\u0441\u043e\u0432\u043e\u0439", "24 horas"}; break;
            case "Use language format": values = new String[]{"Use language format", "\u0424\u043e\u0440\u043c\u0430\u0442 \u043c\u043e\u0432\u0438", "Formato del idioma", "Format de la langue", "Sprachformat", "Format j\u0119zyka", "Formato lingua", "Dil bi\u00e7imi", "\u062a\u0646\u0633\u064a\u0642 \u0627\u0644\u0644\u063a\u0629", "\u0424\u043e\u0440\u043c\u0430\u0442 \u044f\u0437\u044b\u043a\u0430", "Formato do idioma"}; break;
            case "White": values = new String[]{"White", "\u0411\u0456\u043b\u0438\u0439", "Blanco", "Blanc", "Wei\u00df", "Bia\u0142y", "Bianco", "Beyaz", "\u0623\u0628\u064a\u0636", "\u0411\u0435\u043b\u044b\u0439", "Branco"}; break;
            case "Accent": values = new String[]{"Accent", "\u0410\u043a\u0446\u0435\u043d\u0442\u043d\u0438\u0439", "Acento", "Accent", "Akzent", "Akcent", "Accento", "Vurgu", "\u062a\u0645\u064a\u064a\u0632", "\u0410\u043a\u0446\u0435\u043d\u0442", "Destaque"}; break;
            case "Warm": values = new String[]{"Warm", "\u0422\u0435\u043f\u043b\u0438\u0439", "C\u00e1lido", "Chaud", "Warm", "Ciep\u0142y", "Caldo", "S\u0131cak", "\u062f\u0627\u0641\u0626", "\u0422\u0451\u043f\u043b\u044b\u0439", "Quente"}; break;
            case "Soft white": values = new String[]{"Soft white", "\u041c\u2019\u044f\u043a\u0438\u0439 \u0431\u0456\u043b\u0438\u0439", "Blanco suave", "Blanc doux", "Weiches Wei\u00df", "Delikatna biel", "Bianco tenue", "Yumu\u015fak beyaz", "\u0623\u0628\u064a\u0636 \u0647\u0627\u062f\u0626", "\u041c\u044f\u0433\u043a\u0438\u0439 \u0431\u0435\u043b\u044b\u0439", "Branco suave"}; break;
            default: return null;
        }
        for (int i = 0; i < LANG_CODES.length; i++) if (LANG_CODES[i].equals(this.languageCode)) return values[i];
        return values[0];
    }

    private String t(String key) {
        String translated = this.settingsTranslation(key);
        return translated == null ? this.s("", key) : translated;
    }

    private String formattedTime() {
        Locale locale = this.localeForLanguage();
        String mode = this.getPreferences(0).getString("clock_time_mode", "language");
        if ("24".equals(mode)) return new SimpleDateFormat("HH:mm", locale).format(new Date());
        if ("12".equals(mode)) return new SimpleDateFormat("h:mm a", locale).format(new Date());
        return java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT, locale).format(new Date());
    }

    private String formattedDate() {
        return java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL, this.localeForLanguage()).format(new Date());
    }

    private void updateClockViews() {
        int color = this.getPreferences(0).getInt("clock_color", TEXT);
        if (this.clock != null) {
            this.clock.setText(this.formattedTime());
            this.clock.setTextColor(color);
        }
        if (this.dateLine != null) this.dateLine.setText(this.formattedDate());
        this.updateClockSizes();
    }

    private void updateClockSizes() {
        float scale = this.uiScale / 100.0f;
        float base = this.isPortrait() ? 38.0f : 48.0f;
        float ratio = this.clockSize / base;
        if (this.clock != null) this.clock.setTextSize(this.clockSize * scale);
        if (this.dateLine != null) this.dateLine.setTextSize(13.0f * ratio * scale);
        float aodSize = Math.min(104.0f, this.clockSize + 16.0f);
        if (this.aodClock != null) this.aodClock.setTextSize(aodSize * scale);
        if (this.aodDate != null) this.aodDate.setTextSize(16.0f * (aodSize / Math.min(104.0f, base + 16.0f)) * scale);
    }

    private String greeting() {
        int hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY);
        if (hour < 12) return this.t("Good morning");
        if (hour < 18) return this.t("Good afternoon");
        return this.t("Good evening");
    }

    private void updateGreeting() {
        if (this.weatherLine == null) return;
        String summary = this.getPreferences(0).getString("weather_summary", "");
        String city = this.getPreferences(0).getString("weather_city", "").trim();
        String condition = this.getPreferences(0).getString("weather_condition", "");
        boolean configured = !this.getPreferences(0).getString("weather_key", "").trim().isEmpty() && !city.isEmpty() && !summary.isEmpty();
        if (this.weatherIcon != null) {
            this.weatherIcon.setVisibility(configured ? View.VISIBLE : View.GONE);
            if (configured) {
                this.weatherIcon.setText(this.weatherGlyph(condition));
                this.weatherIcon.setTextColor(this.weatherColor(condition));
            }
        }
        if (!configured) this.weatherLine.setText("--");
        else this.weatherLine.setText(this.greeting() + "  ·  " + city + "  ·  " + summary.replaceFirst("^[^\\p{L}\\p{N}]+\\s*", ""));
    }

    private String weatherGlyph(String condition) {
        String c = condition == null ? "" : condition.toLowerCase(Locale.ROOT);
        if (c.contains("thunder")) return "\u26A1";
        if (c.contains("rain") || c.contains("drizzle")) return "\u2602";
        if (c.contains("snow")) return "\u2744";
        if (c.contains("clear")) return "\u2600";
        if (c.contains("cloud")) return "\u2601";
        if (c.contains("mist") || c.contains("fog") || c.contains("haze")) return "\u224b";
        return "\u25cc";
    }

    private int weatherColor(String condition) {
        String c = condition == null ? "" : condition.toLowerCase(Locale.ROOT);
        if (c.contains("clear")) return 0xFFFFD36A;
        if (c.contains("rain") || c.contains("drizzle") || c.contains("cloud")) return 0xFF8DD9F4;
        if (c.contains("snow")) return 0xFFE5F3FF;
        if (c.contains("thunder")) return 0xFFCEB7FF;
        return accentColor;
    }

    private void updateWeatherLine(String ignored) { this.updateGreeting(); }

    private void fetchWeather(boolean force) {
        String apiKey = this.getPreferences(0).getString("weather_key", "").trim();
        String city = this.getPreferences(0).getString("weather_city", "").trim();
        if (apiKey.isEmpty() || city.isEmpty()) { this.updateGreeting(); return; }
        long updateInterval = this.getPreferences(0).getInt("weather_update_minutes", 15) * 60L * 1000L;
        if (!force && System.currentTimeMillis() - this.lastWeatherFetch < updateInterval) return;
        final int request = ++this.weatherRequestId;
        this.lastWeatherFetch = System.currentTimeMillis();
        Thread worker = new Thread(() -> {
            String result = "";
            String conditionResult = "";
            HttpURLConnection connection = null;
            try {
                String encodedCity = URLEncoder.encode(city, "UTF-8");
                String encodedKey = URLEncoder.encode(apiKey, "UTF-8");
                URL geoUrl = new URL("https://api.openweathermap.org/geo/1.0/direct?q=" + encodedCity + "&limit=1&appid=" + encodedKey);
                connection = (HttpURLConnection)geoUrl.openConnection();
                connection.setConnectTimeout(8000); connection.setReadTimeout(8000); connection.setRequestProperty("Accept", "application/json");
                if (connection.getResponseCode() >= 400) throw new java.io.IOException("OpenWeather geocoding failed");
                JSONArray locations = new JSONArray(readText(connection.getInputStream()));
                connection.disconnect(); connection = null;
                if (locations.length() == 0) throw new java.io.IOException("City not found");
                JSONObject location = locations.getJSONObject(0);
                String lang = URLEncoder.encode(this.languageCode.equals("en") ? "en" : this.languageCode, "UTF-8");
                boolean fahrenheit = this.getPreferences(0).getBoolean("weather_fahrenheit", false);
                URL weatherUrl = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + location.getDouble("lat") + "&lon=" + location.getDouble("lon") + "&units=" + (fahrenheit ? "imperial" : "metric") + "&lang=" + lang + "&appid=" + encodedKey);
                connection = (HttpURLConnection)weatherUrl.openConnection();
                connection.setConnectTimeout(8000); connection.setReadTimeout(8000); connection.setRequestProperty("Accept", "application/json");
                if (connection.getResponseCode() >= 400) throw new java.io.IOException("OpenWeather current weather failed");
                JSONObject data = new JSONObject(readText(connection.getInputStream()));
                double temp = data.getJSONObject("main").getDouble("temp");
                String description = data.getJSONArray("weather").getJSONObject(0).optString("description", "");
                String condition = data.getJSONArray("weather").getJSONObject(0).optString("main", "");
                conditionResult = condition;
                result = Math.round(temp) + (fahrenheit ? "°F  " : "°C  ") + description;
            } catch (Exception ignored) {
                result = "";
            } finally { if (connection != null) connection.disconnect(); }
            final String summary = result;
            final String condition = conditionResult;
            this.handler.post(() -> {
                if (request != this.weatherRequestId) return;
                if (!summary.isEmpty()) this.getPreferences(0).edit().putString("weather_summary", summary).putString("weather_condition", condition).apply();
                else this.getPreferences(0).edit().remove("weather_summary").remove("weather_condition").apply();
                this.updateGreeting();
                if ("weather".equals(this.settingsSection) && "settings".equals(this.page)) this.showPage("settings");
            });
        }, "LightLauncher-weather");
        worker.setDaemon(true); worker.start();
    }

    private String readText(InputStream input) throws Exception {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[4096]; int count;
        while ((count = input.read(buffer)) >= 0) out.write(buffer, 0, count);
        input.close(); return out.toString("UTF-8");
    }

    private void updateWallpaperShade() {
        if (this.wallpaperShade != null) {
            int percent = this.getPreferences(0).getInt("wallpaper_dim", 33);
            int alpha = Math.max(0, Math.min(80, percent)) * 255 / 100;
            this.wallpaperShade.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
            this.wallpaperShade.invalidate();
        }
    }

    private int dp(float v) {
        return (int)(v * this.getResources().getDisplayMetrics().density * (float)this.uiScale / 100.0f + 0.5f);
    }

    private boolean isPortrait() {
        return this.getResources().getConfiguration().orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT;
    }

    private void buildShell() {
        this.scene = new FrameLayout((Context)this);
        this.scene.setBackgroundColor(-15394270);
        this.wallpaper = new ImageView((Context)this);
        this.wallpaper.setScaleType(ImageView.ScaleType.CENTER_CROP);
        this.scene.addView((View)this.wallpaper, (ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-1, -1));
        this.wallpaperShade = new View((Context)this);
        this.scene.addView(this.wallpaperShade, (ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-1, -1));
        this.updateWallpaperShade();
        this.root = new LinearLayout((Context)this);
        this.root.setOrientation(1);
        int horizontalPadding=this.isPortrait()?16:34;
        int verticalPadding=this.isPortrait()?10:18;
        this.root.setPadding(this.dp(horizontalPadding), this.dp(verticalPadding), this.dp(horizontalPadding), this.dp(verticalPadding));
        this.root.setBackgroundColor(-1726999267);
        this.root.setClipChildren(false);
        this.root.setClipToPadding(false);
        this.root.setLayoutDirection("ar".equals(this.languageCode) ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        LinearLayout top = new LinearLayout((Context)this);
        top.setGravity(16);
        top.setOrientation(0);
        LinearLayout time = new LinearLayout((Context)this);
        time.setOrientation(1);
        this.clock = this.label("--:--", this.clockSize, -657415, true);
        this.dateLine = this.label("", 13, -4142640, false);
        this.dateLine.setMaxLines(1);
        this.dateLine.setEllipsize(android.text.TextUtils.TruncateAt.END);
        time.addView((View)this.clock);
        time.addView((View)this.dateLine);
        this.weatherIcon = this.label("", 23, accentColor, true);
        this.weatherIcon.setGravity(Gravity.CENTER);
        this.weatherIcon.setVisibility(View.GONE);
        this.weatherLine = this.label("--", 13, -4142640, false);
        this.weatherLine.setMaxLines(2);
        this.weatherLine.setEllipsize(android.text.TextUtils.TruncateAt.END);
        this.weatherLine.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout weatherWidget = new LinearLayout(this);
        weatherWidget.setOrientation(0);
        weatherWidget.setGravity(Gravity.CENTER_VERTICAL);
        weatherWidget.addView(this.weatherIcon, new LinearLayout.LayoutParams(this.dp(36), this.dp(42)));
        weatherWidget.addView(this.weatherLine, new LinearLayout.LayoutParams(0, -2, 1));
        LinearLayout.LayoutParams clockParams = new LinearLayout.LayoutParams(-2, -2);
        clockParams.rightMargin = this.dp(22);
        top.addView(time, clockParams);
        LinearLayout.LayoutParams weatherParams = new LinearLayout.LayoutParams(0, -2, 1);
        weatherParams.rightMargin = this.dp(12);
        top.addView(weatherWidget, weatherParams);
        this.wifiIcon = new StatusIconView((Context)this);
        top.addView((View)this.wifiIcon, (ViewGroup.LayoutParams)this.statusParams());
        Button settingsIcon = this.iconButton("\u2699", this.t("Settings"));
        settingsIcon.setOnClickListener(v -> this.showPage("settings"));
        LinearLayout.LayoutParams sip = this.circleParams();
        sip.leftMargin = this.dp(8.0f);
        top.addView((View)settingsIcon, (ViewGroup.LayoutParams)sip);
        Button add = this.iconButton("+", this.t("Add app"));
        add.setTextSize((float)(23 * this.uiScale) / 100.0f);
        add.setOnClickListener(v -> this.showPage("addapps"));
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
        this.updateClockViews();
        this.updateWeatherLine("--");
    }

    private void resetIdleTimer() {
        this.handler.removeCallbacks(this.idleTimer);
        if (this.aodOverlay == null && this.getPreferences(0).getBoolean("setup_complete", false) && this.aodTimeoutMinutes > 0) {
            this.handler.postDelayed(this.idleTimer, this.aodTimeoutMinutes * 60L * 1000L);
        }
    }

    @Override public void onUserInteraction() {
        super.onUserInteraction();
        if (this.aodOverlay != null) this.exitAod();
        else this.resetIdleTimer();
    }

    private void enterAod() {
        if (this.isFinishing() || this.page.equals("setup") || this.scene == null || this.aodOverlay != null) return;
        this.aodOverlay = new FrameLayout(this);
        this.aodOverlay.setFocusable(true); this.aodOverlay.setClickable(true); this.aodOverlay.setBackgroundColor(Color.BLACK);
        this.aodImage = new ImageView(this); this.aodImage.setScaleType(ImageView.ScaleType.CENTER_CROP); this.aodOverlay.addView(this.aodImage, new FrameLayout.LayoutParams(-1, -1));
        View shade = new View(this); shade.setBackgroundColor(Color.argb(Math.max(0, Math.min(200, this.aodDimPercent * 255 / 100)), 0, 0, 0)); this.aodOverlay.addView(shade, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout center = new LinearLayout(this); center.setOrientation(1); center.setGravity(Gravity.CENTER); center.setPadding(this.dp(32), this.dp(28), this.dp(32), this.dp(28));
        this.aodClock = this.label("--:--", Math.min(104, this.clockSize + 16), this.getPreferences(0).getInt("clock_color", TEXT), true); this.aodClock.setGravity(Gravity.CENTER);
        this.aodDate = this.label("", 16, 0xFFD7DBE7, false); this.aodDate.setGravity(Gravity.CENTER);
        TextView weather = this.label("--", 14, 0xFFBCC3D1, false); weather.setGravity(Gravity.CENTER_VERTICAL); weather.setMaxLines(2);
        String summary = this.getPreferences(0).getString("weather_summary", "");
        String city = this.getPreferences(0).getString("weather_city", "");
        String condition = this.getPreferences(0).getString("weather_condition", "");
        TextView weatherSymbol = this.label("", 24, this.weatherColor(condition), true); weatherSymbol.setGravity(Gravity.CENTER);
        LinearLayout weatherRow = new LinearLayout(this); weatherRow.setOrientation(0); weatherRow.setGravity(Gravity.CENTER);
        weatherRow.addView(weatherSymbol, new LinearLayout.LayoutParams(this.dp(38), -2));
        weatherRow.addView(weather, new LinearLayout.LayoutParams(-2, -2));
        boolean hasWeather = !summary.isEmpty() && !this.getPreferences(0).getString("weather_key", "").isEmpty() && !city.isEmpty();
        if (hasWeather) { weatherSymbol.setText(this.weatherGlyph(condition)); weather.setText(this.greeting() + "  ·  " + city + "  ·  " + summary.replaceFirst("^[^\\p{L}\\p{N}]+\\s*", "")); }
        else weatherSymbol.setVisibility(View.GONE);
        TextView hint = this.label(this.t("Tap anywhere to return"), 11, 0x99FFFFFF, false); hint.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(-1, -2); dp.topMargin = this.dp(8); center.addView(this.aodClock); center.addView(this.aodDate, dp);
        LinearLayout.LayoutParams wp = new LinearLayout.LayoutParams(-1, -2); wp.topMargin = this.dp(14); center.addView(weatherRow, wp);
        this.updateClockSizes();
        LinearLayout.LayoutParams hp = new LinearLayout.LayoutParams(-1, -2); hp.topMargin = this.dp(36); center.addView(hint, hp);
        FrameLayout.LayoutParams cp = new FrameLayout.LayoutParams(-1, -1, Gravity.CENTER); this.aodOverlay.addView(center, cp);
        this.aodOverlay.setOnClickListener(v -> this.exitAod());
        this.scene.addView(this.aodOverlay, new FrameLayout.LayoutParams(-1, -1));
        this.updateAodClock(); this.advanceAodWallpaper();
        if (this.animations) { this.aodOverlay.setAlpha(0f); this.aodOverlay.animate().alpha(1f).setDuration(650L).start(); }
        this.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void updateAodClock() {
        if (this.aodClock == null) return;
        this.aodClock.setText(this.formattedTime()); this.aodClock.setTextColor(this.getPreferences(0).getInt("clock_color", TEXT));
        if (this.aodDate != null) this.aodDate.setText(this.formattedDate());
    }

    private void advanceAodWallpaper() {
        if (this.aodOverlay == null || this.aodImage == null) return;
        this.aodWallpaperIndex = (this.aodWallpaperIndex + 1) % this.aodWallpaperFiles.length;
        Bitmap next = this.decodeAssetWallpaper(this.aodWallpaperFiles[this.aodWallpaperIndex], 2);
        if (next != null) {
            this.aodImage.animate().alpha(0f).setDuration(this.animations ? 450L : 0L).withEndAction(() -> {
                if (this.aodImage != null) { this.aodImage.setImageBitmap(next); this.aodImage.animate().alpha(1f).setDuration(this.animations ? 700L : 0L).start(); }
            }).start();
        }
        this.handler.removeCallbacks(this.aodSlideshow);
        this.handler.postDelayed(this.aodSlideshow, Math.max(10, this.aodSlideSeconds) * 1000L);
    }

    private void exitAod() {
        this.handler.removeCallbacks(this.aodSlideshow);
        if (this.aodOverlay != null) {
            FrameLayout old = this.aodOverlay; this.aodOverlay = null; this.aodImage = null; this.aodClock = null; this.aodDate = null;
            if (this.animations) old.animate().alpha(0f).setDuration(220L).withEndAction(() -> { if (old.getParent() instanceof ViewGroup) ((ViewGroup)old.getParent()).removeView(old); }).start();
            else if (old.getParent() instanceof ViewGroup) ((ViewGroup)old.getParent()).removeView(old);
        }
        this.getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.resetIdleTimer();
    }

    private LinearLayout.LayoutParams statusParams() {
        int size=this.isPortrait()?42:48;
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(this.dp(size), this.dp(size));
        p.leftMargin = this.dp(4.0f);
        return p;
    }

    private LinearLayout.LayoutParams toolParams() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-2, this.dp(44.0f));
        p.rightMargin = this.dp(8.0f);
        return p;
    }

    private LinearLayout.LayoutParams circleParams() {
        int size=this.isPortrait()?44:54;
        return new LinearLayout.LayoutParams(this.dp(size), this.dp(size));
    }

    private Button iconButton(String glyph, String description) {
        Button b = this.button(glyph);
        b.setTextSize((float)(19 * this.uiScale) / 100.0f);
        b.setContentDescription((CharSequence)description);
        b.setBackground((Drawable)this.round(-2144849605, this.dp(28.0f)));
        b.setOnFocusChangeListener((v, f) -> { v.setBackground((Drawable)this.round(f ? -1724099998 : -2144849605, this.dp(28.0f))); if (this.animations) v.animate().scaleX(f ? 1.045f : 1f).scaleY(f ? 1.045f : 1f).setDuration(130L).start(); });
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
        b.setOnFocusChangeListener((v, f) -> { v.setBackground((Drawable)this.round(f ? -1724099998 : -2144849605, this.dp(14.0f))); if (this.animations) v.animate().scaleX(f ? 1.018f : 1f).scaleY(f ? 1.018f : 1f).setDuration(130L).start(); });
        return b;
    }

    private GradientDrawable round(int color2, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color2);
        d.setCornerRadius((float)radius);
        d.setStroke(this.dp(1.0f), color2 == PANEL_FOCUS ? this.accentColor : 1431199081);
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
        if (which.equals("settings")) this.showSettings();
        else if (which.equals("setup")) this.showSetup();
        else if (which.equals("wallpapers")) this.showWallpapers();
        else if (which.equals("onlinewallpapers")) this.showOnlineWallpapers();
        else if (which.equals("languages")) this.showLanguagePicker();
        else if (which.equals("addapps")) this.showAddApps();
        else if (which.equals("faq")) this.showFaq();
        else if (which.equals("logs")) this.showLogs();
        else if (which.equals("apps")) this.showAllApps();
        else this.showHome();
        if (this.animations) {
            this.content.setAlpha(0.0f);
            this.content.animate().alpha(1.0f).setDuration(180L).start();
        } else this.content.setAlpha(1.0f);
        this.resetIdleTimer();
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
            this.addSettingSlider(panel, "UI scale", this.uiScale, SCALE_MIN, SCALE_MAX, "%", value -> { this.uiScale = value; this.getPreferences(0).edit().putInt("ui_scale", value).apply(); }, true);
            addPageButton(panel,this.t("Built-in wallpapers"),()->{this.wallpaperReturnPage="setup";this.showPage("wallpapers");});
        } else {
            addPageButton(panel,this.t("Set as default launcher"),()->this.openDefaultLauncherSettings());
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
        this.languageReturnPage = this.page;
        this.showPage("languages");
    }

    private void showLanguagePicker() {
        LinearLayout panel = this.createPagePanel(this.t("Language"));
        for (int i = 0; i < LANG_CODES.length; i++) {
            final int index = i;
            String mark = LANG_CODES[i].equals(this.languageCode) ? "  ✓" : "";
            this.addPageButton(panel, LANG_NAMES[i] + mark, () -> {
                this.languageCode = LANG_CODES[index];
                this.ukrainian = "uk".equals(this.languageCode);
                this.arabic = "ar".equals(this.languageCode);
                this.getPreferences(0).edit().putString("language", this.languageCode).putBoolean("uk", this.ukrainian).putBoolean("ar", this.arabic).apply();
                this.buildShell();
                this.showPage(this.languageReturnPage);
            });
        }
        this.addPageButton(panel, this.t("Back"), () -> this.showPage(this.languageReturnPage));
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
        switch (file) {
            case "alpine_blossom_pass.jpeg": return "Alpine Blossom Pass";
            case "violet_peak_at_dusk.jpeg": return "Violet Peak at Dusk";
            case "lakeside_mountain_cabin.jpeg": return "Lakeside Mountain Cabin";
            case "golden_hour_tree.webp": return "Golden Hour Tree";
            case "balloon_over_misty_lake.webp": return "Balloon Over Misty Lake";
            case "starfall_reflections.webp": return "Starfall Reflections";
            case "turquoise_alpine_shore.jpeg": return "Turquoise Alpine Shore";
            case "mirror_lake_peaks.jpeg": return "Mirror Lake Peaks";
            case "emerald_valley_waters.jpeg": return "Emerald Valley Waters";
            default:
                String base = file.replaceFirst("(?i)\\.[^.]+$", "").replace('_', ' ').replace('-', ' ');
                return base.substring(0, 1).toUpperCase(localeForLanguage()) + base.substring(1);
        }
    }

    private Bitmap decodeAssetWallpaper(String name,int sample) {
        try{InputStream in=this.getAssets().open("wallpapers/"+name);BitmapFactory.Options o=new BitmapFactory.Options();o.inSampleSize=sample;Bitmap b=BitmapFactory.decodeStream(in,null,o);in.close();return b;}catch(Exception e){return null;}
    }

    private void showFaq() {
        LinearLayout panel=createPagePanel(this.t("FAQ"));
        String[][] qa={
            {"faq_q1","faq_a1"},{"faq_q2","faq_a2"},{"faq_q3","faq_a3"},{"faq_q4","faq_a4"},{"faq_q5","faq_a5"},{"faq_q6","faq_a6"},
            {"How do I find online wallpapers?","Open Settings → Appearance → Find online wallpapers, enter a search, and preview a result before applying it."},
            {"Why does the weather show --?","Set a city and OpenWeather API key in Settings → Weather. The widget stays -- until a request succeeds."},
            {"How often does the weather update?","Choose an interval from 5 to 60 minutes in Settings → Weather, or use Refresh weather now."},
            {"Can I change temperature units?","Open Settings → Weather and switch between Celsius and Fahrenheit."},
            {"How do I change the clock?","Open Settings → Clock to set its size, color, and time format. The preview uses the same clock settings."},
            {"Why does the Wi-Fi icon have different arcs?","The number of lit arcs shows signal strength; its color still shows connection status."},
            {"How do I change the interface size?","Use the 70–150% slider in Settings → Appearance."},
            {"Does Clean memory close every app?","No. It asks Android to stop permitted background processes; Android can keep or restart essential services."}
        };
        for(int i=0;i<qa.length;i++){
            final int n=i+1;
            String question=i<6?this.t("faq_q"+n):qa[i][0];
            String answer=i<6?this.t("faq_a"+n):qa[i][1];
            Button q=this.button(question);q.setGravity(android.view.Gravity.START|android.view.Gravity.CENTER_VERTICAL);q.setPadding(this.dp(18),0,this.dp(12),0);LinearLayout.LayoutParams qp=this.row();qp.leftMargin=this.dp(10);qp.rightMargin=this.dp(10);panel.addView((View)q,(ViewGroup.LayoutParams)qp);
            TextView a=this.label(answer,14,-4142640,false);a.setPadding(this.dp(24),this.dp(8),this.dp(12),this.dp(8));a.setVisibility(View.GONE);LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,-2);ap.leftMargin=this.dp(10);ap.rightMargin=this.dp(10);panel.addView((View)a,ap);
            q.setOnClickListener(v->{boolean open=a.getVisibility()!=View.VISIBLE;a.setVisibility(open?View.VISIBLE:View.GONE);if(this.animations&&open){a.setAlpha(0f);a.animate().alpha(1f).setDuration(160L).start();}});
        }
        addPageButton(panel,this.t("Back"),()->this.showPage(faqReturnPage));
    }

    private void showHome() {
        if(this.isPortrait()){
            this.showHomePortrait();
            return;
        }
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

    private void showHomePortrait() {
        TextView heading=this.label(this.t("Pinned apps"),20,-657415,true);
        heading.setPadding(this.dp(8),this.dp(8),this.dp(8),this.dp(12));
        this.content.addView((View)heading);
        ScrollView scroll=new ScrollView((Context)this);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setClipChildren(false);
        scroll.setClipToPadding(false);
        GridLayout grid=new GridLayout((Context)this);
        grid.setColumnCount(2);
        grid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        grid.setUseDefaultMargins(false);
        grid.setPadding(this.dp(3),this.dp(8),this.dp(3),this.dp(12));
        grid.setClipChildren(false);
        grid.setClipToPadding(false);
        scroll.addView((View)grid,new ScrollView.LayoutParams(-1,-2));
        this.content.addView((View)scroll,new LinearLayout.LayoutParams(-1,0,1.0f));

        Set<String> pins=this.getPins();
        int added=0;
        for(AppEntry app:this.apps){
            if(!pins.contains(app.pkg)||added>=5)continue;
            addPortraitHomeCard(grid,this.createTile(app));
            added++;
        }
        addPortraitHomeCard(grid,this.createCleanMemoryTile());
        addPortraitHomeCard(grid,this.createAllAppsTile());
        if(added==0){
            TextView empty=this.label(this.t("No pinned apps"),14,-4142640,false);
            empty.setPadding(this.dp(10),this.dp(4),this.dp(10),this.dp(8));
            grid.addView((View)empty,new GridLayout.LayoutParams(GridLayout.spec(0),GridLayout.spec(0,2)));
        }
    }

    private void addPortraitHomeCard(GridLayout grid,View card) {
        GridLayout.LayoutParams lp=new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED),GridLayout.spec(GridLayout.UNDEFINED,1f));
        lp.width=0;
        lp.height=this.dp(158);
        lp.setMargins(this.dp(5),this.dp(5),this.dp(5),this.dp(5));
        grid.addView(card,lp);
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
        grid.setNumColumns(this.isPortrait()?2:4);
        grid.setHorizontalSpacing(this.dp(this.isPortrait()?10.0f:18.0f));
        grid.setVerticalSpacing(this.dp(this.isPortrait()?12.0f:18.0f));
        int gridPadding=this.isPortrait()?8:20;
        grid.setPadding(this.dp(gridPadding), this.dp(gridPadding), this.dp(gridPadding), this.dp(this.isPortrait()?12:22));
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
        this.showPage("addapps");
    }

    private void showAddApps() {
        LinearLayout panel = this.createPagePanel(this.t("Choose an app to pin"));
        Set<String> pins = this.getPins();
        int count = 0;
        for (AppEntry app : this.apps) {
            if (pins.contains(app.pkg)) continue;
            count++;
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(0); item.setGravity(16); item.setPadding(this.dp(12), this.dp(8), this.dp(12), this.dp(8));
            item.setFocusable(true); item.setClickable(true); item.setBackground(this.round(PANEL, this.dp(14)));
            ImageView icon = new ImageView(this); icon.setImageDrawable(app.icon); icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            item.addView(icon, new LinearLayout.LayoutParams(this.dp(44), this.dp(44)));
            TextView name = this.label(app.label, 15, TEXT, true); name.setMaxLines(1);
            LinearLayout.LayoutParams np = new LinearLayout.LayoutParams(0, -2, 1); np.leftMargin = this.dp(12); item.addView(name, np);
            TextView add = this.label("+", 24, accentColor, true); item.addView(add);
            item.setOnFocusChangeListener((v, focused) -> {
                v.setBackground(this.round(focused ? PANEL_FOCUS : PANEL, this.dp(14)));
                if (this.animations) v.animate().scaleX(focused ? 1.025f : 1f).scaleY(focused ? 1.025f : 1f).setDuration(130L).start();
            });
            item.setOnClickListener(v -> this.togglePin(app));
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, this.dp(62)); rp.topMargin = this.dp(7); panel.addView(item, rp);
        }
        if (count == 0) panel.addView(this.label(this.t("No apps found"), 15, MUTED, false));
        this.addPageButton(panel, this.t("Back"), () -> this.showPage("pinned"));
    }

    private GradientDrawable settingsTabBackground(boolean active) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.RECTANGLE);
        d.setCornerRadius(this.dp(16));
        int darkAccent = Color.rgb(Color.red(this.accentColor) * 38 / 100, Color.green(this.accentColor) * 38 / 100, Color.blue(this.accentColor) * 38 / 100);
        d.setColor(active ? darkAccent : 0xCC303747);
        d.setStroke(this.dp(active ? 2 : 1), active ? this.accentColor : 0x665C677D);
        return d;
    }

    private void showSettings() {
        LinearLayout frame = new LinearLayout(this);
        frame.setOrientation(1);
        this.content.addView(frame, new LinearLayout.LayoutParams(-1, -1));
        HorizontalScrollView tabScroll = new HorizontalScrollView(this);
        tabScroll.setHorizontalScrollBarEnabled(false);
        LinearLayout tabRow = new LinearLayout(this);
        tabRow.setOrientation(0);
        String[][] sections = {{"general", "General"}, {"appearance", "Appearance"}, {"clock", "Clock"}, {"weather", "Weather"}, {"info", "Info"}};
        Button selectedTab = null;
        for (String[] section : sections) {
            final String id = section[0];
            Button tab = this.button(this.t(section[1]));
            tab.setTextSize(14);
            tab.setPadding(this.dp(18), 0, this.dp(18), 0);
            boolean isSelected = id.equals(this.settingsSection);
            tab.setBackground(this.settingsTabBackground(isSelected));
            tab.setStateListAnimator(null);
            tab.setElevation(0f);
            tab.setScaleX(1f);
            tab.setScaleY(1f);
            tab.setTextColor(isSelected ? Color.WHITE : TEXT);
            tab.setOnFocusChangeListener((v, focused) -> {
                boolean active = focused || id.equals(this.settingsSection);
                v.animate().cancel();
                v.setScaleX(1f);
                v.setScaleY(1f);
                v.setElevation(0f);
                v.setBackground(this.settingsTabBackground(active));
                tab.setTextColor(active ? Color.WHITE : TEXT);
            });
            tab.setOnClickListener(v -> { this.settingsSection = id; this.showPage("settings"); });
            LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(-2, this.dp(46));
            tp.rightMargin = this.dp(8);
            tabRow.addView(tab, tp);
            if (id.equals(this.settingsSection)) selectedTab = tab;
        }
        tabScroll.addView(tabRow);
        LinearLayout.LayoutParams tsp = new LinearLayout.LayoutParams(-1, this.dp(50));
        tsp.bottomMargin = this.dp(6);
        frame.addView(tabScroll, tsp);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(1);
        panel.setPadding(this.dp(20), this.dp(16), this.dp(20), this.dp(20));
        panel.setBackground(this.round(-2144849605, this.dp(20)));
        scroll.addView(panel);
        frame.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        panel.addView(this.label(this.t("Launcher settings") + "  /  " + this.t(this.settingsSection.substring(0, 1).toUpperCase(Locale.ROOT) + this.settingsSection.substring(1)), 19, TEXT, true));
        if (this.settingsSection.equals("appearance")) this.showAppearanceSettings(panel);
        else if (this.settingsSection.equals("clock")) this.showClockSettings(panel);
        else if (this.settingsSection.equals("weather")) this.showWeatherSettings(panel);
        else if (this.settingsSection.equals("info")) this.showInfoSettings(panel);
        else this.showGeneralSettings(panel);
        if (selectedTab != null) selectedTab.requestFocus();
    }

    private void showGeneralSettings(LinearLayout panel) {
        this.addPageButton(panel, this.languageLabel(), () -> { this.languageReturnPage = "settings"; this.showPage("languages"); });
        this.addPageButton(panel, this.t("Set as default launcher"), () -> this.openDefaultLauncherSettings());
        this.addPageButton(panel, this.t("Open Android settings"), () -> this.openSystemSettings("android.settings.SETTINGS"));
        this.addPageButton(panel, this.t("Animations") + ": " + (this.animations ? this.t("On") : this.t("Off")), () -> {
            this.animations = !this.animations;
            this.getPreferences(0).edit().putBoolean("animations", this.animations).apply();
            this.showPage("settings");
        });
        this.addPageButton(panel, this.t("Logs"), () -> this.showPage("logs"));
        this.addPageButton(panel, this.t("FAQ"), () -> { this.faqReturnPage = "settings"; this.showPage("faq"); });
        this.addPageButton(panel, this.t("Refresh app list"), () -> { this.loadApps(); this.showPage("pinned"); });
        panel.addView(this.label(this.t("App version") + ": " + BuildConfig.VERSION_NAME, 12, MUTED, false));
    }

    private static boolean isAccentPaletteColor(int color) {
        for (int option : accentColor_PALETTE) if (option == color) return true;
        return false;
    }

    private String accentName(int index) {
        return this.t(accentColor_NAMES[index]);
    }

    private void addAccentColorPicker(LinearLayout panel) {
        int selected = 0;
        for (int i = 0; i < accentColor_PALETTE.length; i++) if (accentColor_PALETTE[i] == this.accentColor) selected = i;
        final int selectedIndex = selected;
        TextView title = this.label(this.t("Accent color") + ": " + this.accentName(selectedIndex), 14, TEXT, true);
        title.setPadding(0, this.dp(4), 0, this.dp(5));
        panel.addView(title);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(0);
        row.setGravity(Gravity.CENTER_VERTICAL);
        for (int i = 0; i < accentColor_PALETTE.length; i++) {
            final int index = i;
            View swatch = new View(this);
            swatch.setFocusable(true);
            swatch.setClickable(true);
            swatch.setContentDescription(this.accentName(i));
            swatch.setBackground(this.accentSwatchBackground(accentColor_PALETTE[i], i == selected, false));
            swatch.setOnFocusChangeListener((v, focused) -> v.setBackground(this.accentSwatchBackground(accentColor_PALETTE[index], index == selectedIndex, focused)));
            swatch.setOnClickListener(v -> {
                this.accentColor = accentColor_PALETTE[index];
                this.getPreferences(0).edit().putInt("accent_color", this.accentColor).apply();
                this.updateGreeting();
                this.showPage("settings");
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(this.dp(42), this.dp(42));
            lp.rightMargin = this.dp(10);
            row.addView(swatch, lp);
        }
        LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, this.dp(54));
        rp.bottomMargin = this.dp(4);
        panel.addView(row, rp);
    }

    private GradientDrawable accentSwatchBackground(int color, boolean selected, boolean focused) {
        GradientDrawable d = new GradientDrawable();
        d.setShape(GradientDrawable.OVAL);
        d.setColor(color);
        d.setStroke(this.dp(selected ? 4 : focused ? 3 : 1), selected || focused ? Color.WHITE : 0x667C8798);
        return d;
    }

    private void showAppearanceSettings(LinearLayout panel) {
        this.addAccentColorPicker(panel);
        this.addSettingSlider(panel, "UI scale", this.uiScale, SCALE_MIN, SCALE_MAX, "%", value -> {
            this.uiScale = value;
            this.getPreferences(0).edit().putInt("ui_scale", value).apply();
        }, true);
        this.addSettingSlider(panel, "Home wallpaper blur", this.wallpaperBlur, 0, 25, "", value -> {
            this.wallpaperBlur = value;
            this.getPreferences(0).edit().putInt("wallpaper_blur", value).apply();
            if (this.pendingWallpaperApply != null) this.handler.removeCallbacks(this.pendingWallpaperApply);
            this.pendingWallpaperApply = () -> this.applyWallpaper();
            this.handler.postDelayed(this.pendingWallpaperApply, 350L);
        }, false);
        this.addSettingSlider(panel, "Home wallpaper dim", this.getPreferences(0).getInt("wallpaper_dim", 33), 0, 80, "%", value -> {
            this.getPreferences(0).edit().putInt("wallpaper_dim", value).apply();
            this.updateWallpaperShade();
        }, false);
        this.addPageButton(panel, this.t("Built-in wallpapers"), () -> { this.wallpaperReturnPage = "settings"; this.showPage("wallpapers"); });
        this.addPageButton(panel, this.t("Find online wallpapers"), () -> this.showPage("onlinewallpapers"));
        this.addPageButton(panel, this.t("Choose image from storage"), () -> this.pickWallpaper());
        this.addPageButton(panel, this.t("Remove wallpaper"), () -> {
            this.getPreferences(0).edit().remove("wallpaper").apply();
            this.applyWallpaper();
        });
    }

    private void showClockSettings(LinearLayout panel) {
        this.addSettingSlider(panel, "Clock size", this.clockSize, 30, 88, "sp", value -> {
            this.clockSize = value;
            this.getPreferences(0).edit().putInt("clock_size", value).apply();
            this.updateClockSizes();
        }, false);
        String mode = this.getPreferences(0).getString("clock_time_mode", "language");
        String modeLabel = mode.equals("12") ? "12-hour" : mode.equals("24") ? "24-hour" : "Use language format";
        this.addPageButton(panel, this.t("Time format") + ": " + modeLabel, () -> {
            String next = mode.equals("language") ? "12" : mode.equals("12") ? "24" : "language";
            this.getPreferences(0).edit().putString("clock_time_mode", next).apply();
            this.updateClockViews(); this.showPage("settings");
        });
        this.addPageButton(panel, this.t("Clock color") + ": " + this.clockColorName(), () -> this.cycleClockColor());
        this.addPageButton(panel, this.t("Preview always-on display"), () -> this.enterAod());
        this.addSettingSlider(panel, "Always-on display after", this.aodTimeoutMinutes, 0, 60, "min", value -> {
            this.aodTimeoutMinutes = value;
            this.getPreferences(0).edit().putInt("aod_timeout_minutes", value).apply();
            this.resetIdleTimer();
        }, false);
        this.addSettingSlider(panel, "Slideshow interval", this.aodSlideSeconds, 10, 300, "sec", value -> {
            this.aodSlideSeconds = value;
            this.getPreferences(0).edit().putInt("aod_slide_seconds", value).apply();
        }, false);
        this.addSettingSlider(panel, "Always-on display dim", this.aodDimPercent, 0, 80, "%", value -> {
            this.aodDimPercent = value;
            this.getPreferences(0).edit().putInt("aod_dim", value).apply();
        }, false);
    }

    private void showWeatherSettings(LinearLayout panel) {
        String city = this.getPreferences(0).getString("weather_city", "");
        String key = this.getPreferences(0).getString("weather_key", "");
        EditText cityInput = this.editField(this.t("City"), city, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        EditText keyInput = this.editField(this.t("OpenWeather API key"), key, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        panel.addView(cityInput, this.inputParams());
        panel.addView(keyInput, this.inputParams());
        TextView hint = this.label("OpenWeather current weather. API key is saved in this launcher's private settings.", 12, MUTED, false);
        hint.setPadding(0, this.dp(4), 0, this.dp(8));
        panel.addView(hint);
        this.addPageButton(panel, this.t("Save weather settings"), () -> {
            String newCity = cityInput.getText().toString().trim();
            String newKey = keyInput.getText().toString().trim();
            this.getPreferences(0).edit().putString("weather_city", newCity).putString("weather_key", newKey).remove("weather_summary").remove("weather_condition").apply();
            this.lastWeatherFetch = 0;
            this.updateGreeting();
            this.fetchWeather(true);
            Toast.makeText(this, this.t("Weather settings saved"), Toast.LENGTH_SHORT).show();
        });
        this.addSettingSlider(panel, "Weather update interval", this.getPreferences(0).getInt("weather_update_minutes", 15), 5, 60, "min", value -> {
            this.getPreferences(0).edit().putInt("weather_update_minutes", value).apply();
            this.lastWeatherFetch = 0;
        }, false);
        this.addPageButton(panel, this.t("Temperature unit") + ": " + (this.getPreferences(0).getBoolean("weather_fahrenheit", false) ? "°F" : "°C"), () -> {
            boolean fahrenheit = !this.getPreferences(0).getBoolean("weather_fahrenheit", false);
            this.getPreferences(0).edit().putBoolean("weather_fahrenheit", fahrenheit).remove("weather_summary").remove("weather_condition").apply();
            this.lastWeatherFetch = 0;
            this.fetchWeather(true);
            this.showPage("settings");
        });
        this.addPageButton(panel, this.t("Refresh weather now"), () -> { this.lastWeatherFetch = 0; this.fetchWeather(true); });
        this.addPageButton(panel, this.t("Clear weather setup"), () -> {
            this.getPreferences(0).edit().remove("weather_city").remove("weather_key").remove("weather_summary").remove("weather_condition").apply();
            this.lastWeatherFetch = 0; this.updateGreeting(); this.showPage("settings");
        });
    }

    private void showInfoSettings(LinearLayout panel) {
        panel.addView(this.label("LightLauncher", 22, TEXT, true));
        panel.addView(this.label("A personal Android TV launcher by v1dow.", 14, MUTED, false));
        panel.addView(this.label(this.t("App version") + ": " + BuildConfig.VERSION_NAME, 14, MUTED, false));
        panel.addView(this.label(this.t("Build date") + ": " + BuildConfig.BUILD_DATE, 14, MUTED, false));
        this.addPageButton(panel, "Telegram  @v1dow", () -> this.openLink("https://t.me/v1dow"));
        this.addPageButton(panel, "Email  v1dowdev@gmail.com", () -> this.openLink("mailto:v1dowdev@gmail.com"));
        this.addPageButton(panel, "Discord  @v11dow", () -> this.openLink("https://discord.com/app"));
        TextView copyright = this.label("© 2026 v1dow. All rights reserved.\nWallpaper photographs are provided by their respective creators; online wallpaper results show available attribution.", 12, MUTED, false);
        copyright.setPadding(0, this.dp(18), 0, this.dp(8));
        panel.addView(copyright);
    }

    private EditText editField(String hint, String value, int inputType) {
        EditText edit = new EditText(this);
        edit.setSingleLine(true); edit.setText(value); edit.setHint(hint); edit.setTextColor(TEXT); edit.setHintTextColor(MUTED); edit.setInputType(inputType);
        edit.setPadding(this.dp(14), this.dp(7), this.dp(14), this.dp(7));
        edit.setBackground(this.round(0x334B5470, this.dp(12)));
        return edit;
    }

    private LinearLayout.LayoutParams inputParams() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, this.dp(52)); p.topMargin = this.dp(8); return p;
    }

    private String clockColorName() {
        int c = this.getPreferences(0).getInt("clock_color", TEXT);
        return c == -1 ? "White" : c == this.accentColor ? "Accent" : c == 0xFFFFD36A ? "Warm" : "Soft white";
    }

    private void cycleClockColor() {
        int current = this.getPreferences(0).getInt("clock_color", TEXT);
        int next = current == TEXT ? -1 : current == -1 ? this.accentColor : current == this.accentColor ? 0xFFFFD36A : TEXT;
        this.getPreferences(0).edit().putInt("clock_color", next).apply();
        this.updateClockViews(); this.showPage("settings");
    }

    private void openLink(String uri) {
        try { this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri))); }
        catch (Exception e) { Toast.makeText(this, this.t("Could not open link"), Toast.LENGTH_SHORT).show(); }
    }

    private void addSettingSlider(LinearLayout panel, String title, int initial, int min, int max, String suffix, java.util.function.IntConsumer save, boolean rebuildForScale) {
        LinearLayout block = new LinearLayout(this); block.setOrientation(1); block.setPadding(this.dp(14), this.dp(10), this.dp(14), this.dp(8)); block.setBackground(this.round(0x334B5470, this.dp(15)));
        LinearLayout heading = new LinearLayout(this); heading.setGravity(16); heading.setOrientation(0);
        TextView name = this.label(this.t(title), 14, TEXT, true); heading.addView(name, new LinearLayout.LayoutParams(0, -2, 1));
        TextView valueText = this.label(initial + suffix, 14, accentColor, true); heading.addView(valueText);
        block.addView(heading);
        RangeSlider slider = new RangeSlider(this, min, max, initial);
        block.addView(slider, new LinearLayout.LayoutParams(-1, this.dp(44)));
        LinearLayout.LayoutParams blockp = new LinearLayout.LayoutParams(-1, -2); blockp.topMargin = this.dp(8); panel.addView(block, blockp);
        SliderValueListener listener = (v, finished) -> {
            valueText.setText(v + suffix); save.accept(v);
            if (rebuildForScale && finished) {
                if (this.pendingScaleRebuild != null) this.handler.removeCallbacks(this.pendingScaleRebuild);
                String returnPage = this.page;
                this.pendingScaleRebuild = () -> { this.buildShell(); this.showPage(returnPage); };
                this.handler.postDelayed(this.pendingScaleRebuild, 250L);
            }
        };
        slider.setValueListener(listener);
    }

    private void showOnlineWallpapers() {
        LinearLayout panel = this.createPagePanel(this.t("Find online wallpapers"));
        EditText search = this.editField(this.t("Search landscapes, cities, colors…"), "", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        EditText directUrl = this.editField(this.t("Or paste a direct HTTPS image URL"), "", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        panel.addView(search, this.inputParams());
        panel.addView(directUrl, this.inputParams());
        LinearLayout results = new LinearLayout(this); results.setOrientation(1);
        this.addPageButton(panel, this.t("Search wallpapers"), () -> this.searchOnlineWallpapers(search.getText().toString().trim(), results));
        this.addPageButton(panel, this.t("Download direct URL"), () -> this.downloadDirectWallpaper(directUrl.getText().toString().trim()));
        TextView attribution = this.label("Searches Wikimedia Commons. Results are filtered to landscape images at least 1920 × 1080; preview the image and license before applying.", 12, MUTED, false);
        attribution.setPadding(0, this.dp(8), 0, this.dp(4)); panel.addView(attribution);
        panel.addView(results, new LinearLayout.LayoutParams(-1, -2));
        this.addPageButton(panel, this.t("Back"), () -> this.showPage("settings"));
        String savedSearch = this.getPreferences(0).getString("wallpaper_search", "").trim();
        if (savedSearch.isEmpty()) savedSearch = "nature";
        search.setText(savedSearch);
        this.searchOnlineWallpapers(savedSearch, results);
    }

    private void downloadDirectWallpaper(String address) {
        if (address == null || !address.toLowerCase(Locale.ROOT).startsWith("https://")) {
            Toast.makeText(this, this.t("Enter a valid HTTPS image URL"), Toast.LENGTH_SHORT).show();
            return;
        }
        this.downloadOnlineWallpaper(new OnlineWallpaper("Wallpaper", address, address, "User supplied", "", 0, 0));
    }

    private void searchOnlineWallpapers(String query, LinearLayout results) {
        query = query.trim();
        if (query.isEmpty()) { Toast.makeText(this, this.t("Enter a search"), Toast.LENGTH_SHORT).show(); return; }
        final String searchTerm = query;
        this.getPreferences(0).edit().putString("wallpaper_search", searchTerm).apply();
        results.removeAllViews(); results.addView(this.label(this.t("Searching…"), 14, MUTED, false));
        Thread worker = new Thread(() -> {
            ArrayList<OnlineWallpaper> found = new ArrayList<>();
            HttpURLConnection connection = null;
            String error = "";
            try {
                String encoded = URLEncoder.encode("wallpaper landscape " + searchTerm, "UTF-8");
                URL url = new URL("https://commons.wikimedia.org/w/api.php?action=query&generator=search&gsrsearch=" + encoded + "&gsrnamespace=6&gsrlimit=30&prop=imageinfo&iiprop=url%7Csize%7Cextmetadata&iiurlwidth=500&format=json&formatversion=2");
                connection = (HttpURLConnection)url.openConnection();
                connection.setConnectTimeout(10000); connection.setReadTimeout(12000); connection.setRequestProperty("User-Agent", "LightLauncher/1.0 (Android TV wallpaper picker)"); connection.setRequestProperty("Accept", "application/json");
                if (connection.getResponseCode() >= 400) throw new java.io.IOException("Wallpaper search failed");
                JSONObject root = new JSONObject(this.readText(connection.getInputStream()));
                JSONObject queryObject = root.optJSONObject("query");
                JSONArray pages = queryObject == null ? null : queryObject.optJSONArray("pages");
                if (pages != null) for (int i = 0; i < pages.length(); i++) {
                    JSONObject page = pages.optJSONObject(i); if (page == null) continue;
                    JSONArray infos = page.optJSONArray("imageinfo"); if (infos == null || infos.length() == 0) continue;
                    JSONObject info = infos.optJSONObject(0); if (info == null) continue;
                    String original = info.optString("url", ""); String thumb = info.optString("thumburl", original);
                    if (!original.startsWith("https://") || !thumb.startsWith("https://")) continue;
                    int width = info.optInt("width", 0), height = info.optInt("height", 0);
                    if (width < 1920 || height < 1080) continue;
                    JSONObject metadata = info.optJSONObject("extmetadata");
                    JSONObject license = metadata == null ? null : metadata.optJSONObject("LicenseShortName");
                    JSONObject artist = metadata == null ? null : metadata.optJSONObject("Artist");
                    String licenseName = license == null ? "License details" : license.optString("value", "License details").replaceAll("<[^>]+>", "").trim();
                    String artistName = artist == null ? "" : artist.optString("value", "").replaceAll("(?i)<br\\s*/?>", " ").replaceAll("<[^>]+>", "").replace("&nbsp;", " ").trim();
                    found.add(new OnlineWallpaper(page.optString("title", "Wallpaper"), thumb, original, licenseName, artistName, width, height));
                }
            } catch (Exception e) { error = e.getClass().getSimpleName(); }
            finally { if (connection != null) connection.disconnect(); }
            final String failure = error;
            this.handler.post(() -> {
                if (!this.page.equals("onlinewallpapers")) return;
                results.removeAllViews();
                if (found.isEmpty()) {
                    results.addView(this.label(failure.isEmpty() ? this.t("No online images found") : this.t("Search failed — check your connection"), 14, MUTED, false));
                    return;
                }
                for (OnlineWallpaper item : found) this.addOnlineWallpaperCard(results, item);
            });
        }, "LightLauncher-wallpaper-search");
        worker.setDaemon(true); worker.start();
    }

    private void addOnlineWallpaperCard(LinearLayout parent, OnlineWallpaper result) {
        LinearLayout card = new LinearLayout(this); card.setOrientation(1); card.setFocusable(true); card.setClickable(true); card.setPadding(this.dp(8), this.dp(8), this.dp(8), this.dp(8)); card.setBackground(this.round(PANEL, this.dp(14)));
        ImageView preview = new ImageView(this); preview.setScaleType(ImageView.ScaleType.CENTER_CROP); preview.setBackgroundColor(0x55333B4B); preview.setContentDescription(result.title);
        card.addView(preview, new LinearLayout.LayoutParams(-1, this.dp(150)));
        TextView title = this.label(result.title.replace("File:", ""), 13, TEXT, true); title.setMaxLines(2); title.setPadding(this.dp(4), this.dp(6), this.dp(4), 0); card.addView(title);
        String credit = result.width > 0 ? result.width + " × " + result.height + " px  ·  " : "";
        credit += result.artist.isEmpty() ? this.t("License") + ": " + result.license : this.t("By") + " " + result.artist + "  ·  " + result.license;
        TextView license = this.label(credit, 11, MUTED, false); license.setMaxLines(3); license.setPadding(this.dp(4), this.dp(2), this.dp(4), 0); card.addView(license);
        card.setOnFocusChangeListener((v, focused) -> { v.setBackground(this.round(focused ? PANEL_FOCUS : PANEL, this.dp(14))); if (this.animations) v.animate().scaleX(focused ? 1.015f : 1f).scaleY(focused ? 1.015f : 1f).setDuration(120L).start(); });
        card.setOnClickListener(v -> this.downloadOnlineWallpaper(result));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2); cp.topMargin = this.dp(8); parent.addView(card, cp);
        Thread imageWorker = new Thread(() -> {
            Bitmap bitmap = null; HttpURLConnection c = null;
            try { c = (HttpURLConnection)new URL(result.thumbnail).openConnection(); c.setConnectTimeout(8000); c.setReadTimeout(10000); c.setRequestProperty("User-Agent", "LightLauncher/1.0"); if (c.getResponseCode() < 400) bitmap = BitmapFactory.decodeStream(c.getInputStream()); }
            catch (Exception ignored) {} finally { if (c != null) c.disconnect(); }
            final Bitmap decoded = bitmap; this.handler.post(() -> { if (decoded != null && preview.getParent() != null) preview.setImageBitmap(decoded); });
        }, "LightLauncher-wallpaper-preview"); imageWorker.setDaemon(true); imageWorker.start();
    }

    private void downloadOnlineWallpaper(OnlineWallpaper image) {
        Toast.makeText(this, this.t("Downloading wallpaper…"), Toast.LENGTH_SHORT).show();
        Thread worker = new Thread(() -> {
            File folder = new File(this.getFilesDir(), "wallpapers");
            File target = new File(folder, "online_wallpaper.img");
            HttpURLConnection c = null; String error = "";
            try {
                if (!folder.exists() && !folder.mkdirs()) throw new java.io.IOException("Cannot create wallpaper folder");
                c = (HttpURLConnection)new URL(image.original).openConnection(); c.setConnectTimeout(10000); c.setReadTimeout(20000); c.setRequestProperty("User-Agent", "LightLauncher/1.0");
                if (c.getResponseCode() >= 400 || !c.getContentType().toLowerCase(Locale.ROOT).startsWith("image/")) throw new java.io.IOException("Invalid image response");
                if (c.getContentLengthLong() > 25000000L) throw new java.io.IOException("Image is too large");
                InputStream in = c.getInputStream(); FileOutputStream out = new FileOutputStream(target); byte[] buf = new byte[8192]; int n; long total = 0;
                while ((n = in.read(buf)) >= 0) { total += n; if (total > 25000000L) throw new java.io.IOException("Image is too large"); out.write(buf, 0, n); }
                out.close(); in.close();
            } catch (Exception e) { error = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage(); }
            finally { if (c != null) c.disconnect(); }
            final String failure = error;
            this.handler.post(() -> {
                if (failure.isEmpty()) {
                    this.getPreferences(0).edit().putString("wallpaper", "download:online_wallpaper.img").apply();
                    this.applyWallpaper(); this.showPage("settings"); Toast.makeText(this, this.t("Wallpaper updated"), Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this, this.t("Download failed"), Toast.LENGTH_LONG).show();
            });
        }, "LightLauncher-wallpaper-download"); worker.setDaemon(true); worker.start();
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
        if(value.startsWith("download:"))return new FileInputStream(new File(new File(this.getFilesDir(), "wallpapers"), value.substring(9)));
        if(value.startsWith("file:"))return new FileInputStream(new File(Uri.parse(value).getPath()));
        return this.getContentResolver().openInputStream(Uri.parse(value));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Bitmap blurWallpaper(Bitmap source) {
        if (source == null) {
            return null;
        }
        if (this.wallpaperBlur <= 0) return source;
        RenderScript rs = null;
        Allocation input = null;
        Allocation output = null;
        try {
            rs = RenderScript.create((Context)this.getApplicationContext());
            input = Allocation.createFromBitmap((RenderScript)rs, (Bitmap)source);
            Bitmap blurred = Bitmap.createBitmap((int)source.getWidth(), (int)source.getHeight(), (Bitmap.Config)Bitmap.Config.ARGB_8888);
            output = Allocation.createFromBitmap((RenderScript)rs, (Bitmap)blurred);
            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create((RenderScript)rs, (Element)Element.U8_4((RenderScript)rs));
            blur.setRadius((float)Math.max(1, Math.min(25, this.wallpaperBlur)));
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

    private void openDefaultLauncherSettings() {
        Intent homeSettings = new Intent("android.settings.HOME_SETTINGS");
        try {
            if (homeSettings.resolveActivity(this.getPackageManager()) != null) {
                this.startActivity(homeSettings);
                this.appendSettingsLog("Opened Android Home app selection");
                return;
            }
        } catch (Exception e) { this.appendSettingsLog("Home settings action failed: " + e.getClass().getSimpleName()); }
        try {
            this.startActivity(new Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"));
            this.appendSettingsLog("Opened Android default apps settings");
        } catch (Exception e) {
            this.appendSettingsLog("Default apps settings unavailable; opening Android settings");
            this.openSystemSettings("android.settings.SETTINGS");
        }
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
        int level = 0;
        try {
            boolean connected;
            ConnectivityManager cm = (ConnectivityManager)this.getSystemService("connectivity");
            NetworkInfo w = cm.getNetworkInfo(1);
            NetworkInfo active = cm.getActiveNetworkInfo();
            boolean bl = connected = w != null && w.isConnected();
            if (connected) {
                status = 1;
                WifiManager wifi = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifi != null && wifi.getConnectionInfo() != null) level = WifiManager.calculateSignalLevel(wifi.getConnectionInfo().getRssi(), 5);
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
        this.wifiIcon.setStatus(status, level);
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
        if (this.aodOverlay != null) {
            this.exitAod();
        } else if (this.page.equals("languages")) {
            this.showPage(this.languageReturnPage);
        } else if (this.page.equals("onlinewallpapers")) {
            this.showPage("settings");
        } else if (this.page.equals("addapps")) {
            this.showPage("pinned");
        } else if (this.page.equals("setup")) {
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
        this.handler.removeCallbacks(this.idleTimer);
        this.handler.removeCallbacks(this.aodSlideshow);
        this.weatherRequestId++;
        this.getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

    private static final class OnlineWallpaper {
        final String title, thumbnail, original, license, artist;
        final int width, height;
        OnlineWallpaper(String title, String thumbnail, String original, String license, String artist, int width, int height) {
            this.title = title; this.thumbnail = thumbnail; this.original = original; this.license = license; this.artist = artist; this.width = width; this.height = height;
        }
    }

    private interface SliderValueListener { void onValueChanged(int value, boolean finished); }

    private final class RangeSlider extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int min, max;
        private int value;
        private SliderValueListener listener;
        private float downX;
        RangeSlider(Context context, int min, int max, int value) {
            super(context); this.min = min; this.max = max; this.value = Math.max(min, Math.min(max, value));
            setFocusable(true); setContentDescription("Value slider, " + min + " to " + max);
            setOnFocusChangeListener((v, focused) -> invalidate());
        }
        void setValueListener(SliderValueListener listener) { this.listener = listener; }
        int getValue() { return this.value; }
        void adjustBy(int delta, boolean finished) { this.setValue(this.value + delta, finished); }
        private void setValue(int newValue, boolean finished) {
            int bounded = Math.max(this.min, Math.min(this.max, newValue));
            if (bounded == this.value && !finished) return;
            this.value = bounded; invalidate();
            if (this.listener != null) this.listener.onValueChanged(this.value, finished);
        }
        private void setFromPosition(float x, boolean finished) {
            float left = this.dp(12), right = Math.max(left + 1, getWidth() - this.dp(12));
            float fraction = Math.max(0f, Math.min(1f, (x - left) / (right - left)));
            this.setValue(this.min + Math.round(fraction * (this.max - this.min)), finished);
        }
        private int dp(float value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }
        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float left = this.dp(12), right = Math.max(left + 1, getWidth() - this.dp(12)), y = getHeight() / 2f;
            float fraction = (this.value - this.min) / (float)Math.max(1, this.max - this.min), thumb = left + (right - left) * fraction;
            this.paint.setStyle(Paint.Style.STROKE); this.paint.setStrokeCap(Paint.Cap.ROUND); this.paint.setStrokeWidth(this.dp(4));
            this.paint.setColor(0x66515C70); canvas.drawLine(left, y, right, y, this.paint);
            this.paint.setColor(accentColor); canvas.drawLine(left, y, thumb, y, this.paint);
            this.paint.setStyle(Paint.Style.FILL); this.paint.setColor(this.hasFocus() ? Color.WHITE : 0xFFE6E9F2); canvas.drawCircle(thumb, y, this.dp(this.hasFocus() ? 9 : 8), this.paint);
            this.paint.setColor(accentColor); canvas.drawCircle(thumb, y, this.dp(4), this.paint);
        }
        @Override public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) { this.downX = event.getX(); requestFocus(); this.setFromPosition(this.downX, false); return true; }
            if (event.getAction() == MotionEvent.ACTION_MOVE) { this.setFromPosition(event.getX(), false); return true; }
            if (event.getAction() == MotionEvent.ACTION_UP) { this.setFromPosition(event.getX(), true); performClick(); return true; }
            return true;
        }
        @Override public boolean performClick() { super.performClick(); return true; }
        @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) { this.adjustBy(-1, true); return true; }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) { this.adjustBy(1, true); return true; }
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) { return true; }
            return super.onKeyDown(keyCode, event);
        }
    }

    private class StatusIconView
    extends FrameLayout {
        private int status;
        private int signalLevel;
        private final IconDrawing icon;

        StatusIconView(Context c) {
            super(c);
            this.status = 0;
            this.signalLevel = 0;
            this.setFocusable(true);
            this.setClickable(true);
            this.setContentDescription("Wi-Fi settings, connection status, and signal strength");
            this.setBackground((Drawable)MainActivity.this.round(-2144849605, MainActivity.this.dp(24.0f)));
            this.icon = new IconDrawing(c, this);
            this.addView(this.icon, (ViewGroup.LayoutParams)new FrameLayout.LayoutParams(-1, -1));
            this.setOnFocusChangeListener((v, f) -> this.setBackground((Drawable)MainActivity.this.round(f ? -1724099998 : -2144849605, MainActivity.this.dp(24.0f))));
            this.setOnClickListener(v -> MainActivity.this.openSystemSettings("android.settings.WIFI_SETTINGS"));
        }

        void setStatus(int value, int level) {
            if (this.status != value || this.signalLevel != level) {
                this.status = value;
                this.signalLevel = level;
                String state = value == 2 ? "internet connected" : value == 1 ? "connected, internet unverified" : "disconnected";
                this.setContentDescription("Wi-Fi " + state + ", signal " + level + " of 4");
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
            int color = this.parent.status == 2 ? -9248846 : (this.parent.status == 1 ? -11930 : -30080);
            this.p.setColor(0x667A8496);
            this.p.setStrokeWidth((float)MainActivity.this.dp(2.4f));
            this.p.setStyle(Paint.Style.STROKE);
            this.p.setStrokeCap(Paint.Cap.ROUND);
            float x = (float)this.getWidth() / 2.0f;
            float y = (float)this.getHeight() / 2.0f;
            c.drawArc(x - (float)MainActivity.this.dp(16.0f), y - (float)MainActivity.this.dp(15.0f), x + (float)MainActivity.this.dp(16.0f), y + (float)MainActivity.this.dp(16.0f), 220.0f, 100.0f, false, this.p);
            c.drawArc(x - (float)MainActivity.this.dp(11.0f), y - (float)MainActivity.this.dp(10.0f), x + (float)MainActivity.this.dp(11.0f), y + (float)MainActivity.this.dp(14.0f), 220.0f, 100.0f, false, this.p);
            c.drawArc(x - (float)MainActivity.this.dp(6.0f), y - (float)MainActivity.this.dp(5.0f), x + (float)MainActivity.this.dp(6.0f), y + (float)MainActivity.this.dp(11.0f), 220.0f, 100.0f, false, this.p);
            this.p.setColor(color);
            if (this.parent.signalLevel >= 3) c.drawArc(x - (float)MainActivity.this.dp(16.0f), y - (float)MainActivity.this.dp(15.0f), x + (float)MainActivity.this.dp(16.0f), y + (float)MainActivity.this.dp(16.0f), 220.0f, 100.0f, false, this.p);
            if (this.parent.signalLevel >= 2) c.drawArc(x - (float)MainActivity.this.dp(11.0f), y - (float)MainActivity.this.dp(10.0f), x + (float)MainActivity.this.dp(11.0f), y + (float)MainActivity.this.dp(14.0f), 220.0f, 100.0f, false, this.p);
            if (this.parent.signalLevel >= 1) c.drawArc(x - (float)MainActivity.this.dp(6.0f), y - (float)MainActivity.this.dp(5.0f), x + (float)MainActivity.this.dp(6.0f), y + (float)MainActivity.this.dp(11.0f), 220.0f, 100.0f, false, this.p);
            this.p.setStyle(Paint.Style.FILL);
            c.drawCircle(x, y + (float)MainActivity.this.dp(8.0f), (float)MainActivity.this.dp(this.parent.signalLevel >= 4 ? 3.2f : 2.2f), this.p);
        }
    }
}
