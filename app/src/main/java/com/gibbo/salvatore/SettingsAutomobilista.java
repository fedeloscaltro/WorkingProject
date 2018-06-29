package com.gibbo.salvatore;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsAutomobilista extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            final SwitchPreference filterSwitch = (SwitchPreference) findPreference("switch_filter");
            final ListPreference carburante = (ListPreference) findPreference("pref_fuel");
            final EditTextPreference minPrice = (EditTextPreference) findPreference("pref_prezzo_minimo");
            final EditTextPreference maxPrice = (EditTextPreference) findPreference("pref_prezzo_massimo");

            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            final SharedPreferences.Editor editor = mPreferences.edit();

            editor.putBoolean("switchFilter", filterSwitch.isChecked());
            editor.commit();
            editor.putString("favFuel", carburante.getValue());
            editor.commit();
            editor.putString("minPrice", minPrice.getText());
            editor.commit();
            editor.putString("maxPrice", maxPrice.getText());
            editor.commit();

            filterSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if(filterSwitch.isChecked()) {
                        filterSwitch.setChecked(false);
                        editor.putBoolean("switchFilter", false);
                        editor.commit();
                        /*MapsFragment fragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.maps_fragment);
                    fragment.aggiornaMappa();*/
                    }
                    else {
                        filterSwitch.setChecked(true);
                        editor.putBoolean("switchFilter", true);
                        editor.commit();
                        /*MapsFragment fragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.maps_fragment);
                    fragment.aggiornaMappa();*/
                    }
                    return true;
                }
            });
            carburante.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    editor.putString("favFuel", carburante.getValue());
                    editor.commit();
                    /*MapsFragment fragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.maps_fragment);
                    fragment.aggiornaMappa();*/
                    return true;
                }
            });

            minPrice.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    editor.putString("minPrice", minPrice.getText());
                    editor.commit();
                    /*MapsFragment fragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.maps_fragment);
                    fragment.aggiornaMappa();*/
                    return true;
                }
            });

            maxPrice.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    editor.putString("maxPrice", maxPrice.getText());
                    editor.commit();
                    /*MapsFragment fragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.maps_fragment);
                    fragment.aggiornaMappa();*/
                    return true;
                }
            });


            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("pref_prezzo_minimo"));
            bindPreferenceSummaryToValue(findPreference("pref_prezzo_massimo"));
            //bindPreferenceSummaryToValue(findPreference("pref_distanza_massima"));
            bindPreferenceSummaryToValue(findPreference("pref_fuel"));

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsAutomobilista.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        private SwitchPreference switchPref;

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            final SwitchPreference switchPreference = (SwitchPreference) findPreference("notifications_new_message");
            /*if(switchPreference.getSharedPreferences().getBoolean("notifications_new_message", true)){
                Toast.makeText(getContext(), "true", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "false", Toast.LENGTH_LONG).show();
            }*/

            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            final SharedPreferences.Editor editor = mPreferences.edit();

            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                        if (switchPreference.isChecked()) {
                            //Toast.makeText(getContext(), "false", Toast.LENGTH_LONG).show();
                            switchPreference.setChecked(false);

                            /*Fragment mapsFragment = new MapsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("switchPreference", switchPreference.isChecked());
                            mapsFragment.setArguments(bundle);*/
                            //showFragment();
                            /*editor.putBoolean("switchPreference", false);
                            editor.commit();*/
                        } else {
                            //Toast.makeText(getContext(), "true", Toast.LENGTH_LONG).show();
                            switchPreference.setChecked(true);

                            /*Fragment mapsFragment = new MapsFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("switchPreference", switchPreference.isChecked());
                            mapsFragment.setArguments(bundle);*/
                            /*editor.putBoolean("switchPreference", true);
                            editor.commit();*/
                        }
                    return true;
                }
            });


            final SwitchPreference vibrationPreference = (SwitchPreference) findPreference("notifications_new_message_vibrate");

            vibrationPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object isVibrateOnObject) {
                    boolean isVibrateOn = (Boolean) isVibrateOnObject;
                    if (isVibrateOn) {
                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        if (v != null) {
                            v.vibrate(400);
                            editor.putBoolean("isVibrateOn", true);
                            editor.commit();
                        }
                    } else {
                        editor.putBoolean("isVibrateOn", false);
                        editor.commit();
                    }
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsAutomobilista.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsAutomobilista.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
