package com.iqiyi.qigsaw.sample;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.splitinstall.SplitInstallException;
import com.google.android.play.core.splitinstall.SplitInstallManager;
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode;
import com.google.android.play.core.splitinstall.testing.FakeSplitInstallManagerFactory;
import com.google.android.play.core.splitinstall.SplitInstallRequest;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnFailureListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseSplitActivity implements View.OnClickListener {

    private static final String TAG = "DynamicFeatures";
    Handler mHandler = new Handler();
    private static final String JAVA_SAMPLE_ACTIVITY = "com.iqiyi.qigsaw.sample.java.JavaSampleActivity";

    private static final String NATIVE_SAMPLE_ACTIVITY = "com.iqiyi.qigsaw.sample.ccode.NativeSampleActivity";
    private static final String INSTALL_SAMPLE_ACTIVITY = "com.microsoft.office.install.InstallSampleActivity";
    private SplitInstallManager installManager;

    private LinearLayout buttonGroups;

    private LinearLayout progressbarGroups;
    private ProgressBar mProgress;
    private TextView mProgressText;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.00");

    private TextView progressText;

    private String moduleJava;
    long time1,time2,time3,time4,time5;
    private String moduleAssets;
    private String moduleInstallTime;
    private String moduleNative;
    private String tag="anushka stateupdate";
    private SplitInstallStateUpdatedListener myListener = new SplitInstallStateUpdatedListener() {

        @Override
        public void onStateUpdate(SplitInstallSessionState state) {
            boolean multiInstall = state.moduleNames().size() > 1;
            if(state.status()==SplitInstallSessionStatus.DOWNLOADING){
//                if(state.moduleNames().get(0)=="native") {
//                    time1 = System.currentTimeMillis();
//                }
//                else{
//                    time4=System.currentTimeMillis();
//                }
                time1=System.currentTimeMillis()/1000;
                onDownloading(state,state.moduleNames().get(0));
                Log.d(tag,state.moduleNames().get(0)+" is downloading");
            }
            else if(state.status()==SplitInstallSessionStatus.DOWNLOADED){
                time2=System.currentTimeMillis();
                Log.d(tag,state.moduleNames().get(0)+" downloaded in "+ (time2-time1)+" seconds");
                onDownloaded(state.moduleNames().get(0));
            }
            else if(state.status()==SplitInstallSessionStatus.INSTALLING){
                onInstalling(state.moduleNames().get(0));
            }
            else if (state.status() == SplitInstallSessionStatus.INSTALLED) {
                Log.d(tag,state.moduleNames().get(0)+" is installed");
               time3=System.currentTimeMillis()/1000;
                Log.d(tag,state.moduleNames().get(0)+" installed in "+ (time3-time1)+" seconds");
                if (installManager.getInstalledModules().contains(moduleJava)) {
                    mProgress.setVisibility(View.INVISIBLE);
                    mProgressText.setVisibility(View.INVISIBLE);
                    onSuccessfullyLoad(moduleJava, !multiInstall);

                    //toastAndLog(moduleName + " has been installed!!!!!");
                }
                else if (installManager.getInstalledModules().contains(moduleNative)) {
                    //onSuccessfullyLoad(moduleJava, !multiInstall);
                    toastAndLog("Background module has been installed");
                    findViewById(R.id.btn_load_native).setEnabled(true);
                }
            } else if (state.status() == SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION) {
                try {
                    startIntentSender(state.resolutionIntent().getIntentSender(), null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    @SuppressLint("StringFormatInvalid")
    private void onDownloading(SplitInstallSessionState state, String name) {
        if(name.equals(moduleJava)) {
            mProgress.setVisibility(View.VISIBLE);
            mProgressText.setVisibility(View.VISIBLE);
            String test = "test anushka";
            //mProgress.setProgress(Long.valueOf(state.bytesDownloaded()).intValue());
            mProgress.setMax(Integer.parseInt(String.valueOf(state.totalBytesToDownload())));
            mProgress.setProgress(Integer.parseInt(String.valueOf(state.bytesDownloaded())));
            double progress = (double) ((state.bytesDownloaded() * 100) / state.totalBytesToDownload());
            //int progress = Integer.parseInt(String.valueOf(state.totalBytesToDownload()))/Integer.parseInt(String.valueOf(state.bytesDownloaded()));
            //toastAndLog("progress "+ progress);
            Log.d(test, "progress " + progress);
            Log.d(test, "bytes downloaded " + state.bytesDownloaded());
            Log.d(test, "total bytes to download  " + state.totalBytesToDownload());



            updateProgressMessage(getString(R.string.installer_downloading) + decimalFormat.format(progress) + "%");
            //updateProgressMessage(getString(R.string.installer_downloading) + progress + "%");
        }
    }


    private void onDownloaded(String name) {
        //updateProgressMessage(getString(R.string.installer_downloaded));
        if (name.equals(moduleJava)) {
            mProgress.setVisibility(View.VISIBLE);
            mProgressText.setVisibility(View.VISIBLE);
            updateProgressMessage("Download complete!");
        }
        Log.d(tag,"downloaded");
    }


    private void onInstalling(String name) {
            if(name.equals(moduleJava)) {
                mProgress.setVisibility(View.VISIBLE);
                mProgressText.setVisibility(View.VISIBLE);
                updateProgressMessage("Installing");
            }
        Log.d(tag,"installing");

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        installManager = SplitInstallManagerFactory.create(this);
//        Context context = null;
//        try {
//            context = createPackageContext(getPackageName(), 0);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
        //}
        //installManager=FakeSplitInstallManagerFactory.create(context,context.getExternalFilesDir("local_testing"));
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_load_java).setOnClickListener(this);
        //findViewById(R.id.btn_load_assets).setOnClickListener(this);
        findViewById(R.id.btn_load_native).setOnClickListener(this);
        findViewById(R.id.btn_load_install).setOnClickListener(this);
        //findViewById(R.id.btn_install_all_now).setOnClickListener(this);
        //findViewById(R.id.btn_install_all_deferred).setOnClickListener(this);
        //findViewById(R.id.btn_uninstall_all_deferred).setOnClickListener(this);
        buttonGroups = findViewById(R.id.button_groups);
        progressbarGroups = findViewById(R.id.progress_bar_groups);
        progressText = findViewById(R.id.progress_text);
        mProgress = findViewById(R.id.qigsaw_installer_progress);
        mProgressText = findViewById(R.id.qigsaw_installer_status);
        moduleJava = getString(R.string.module_feature_java);
        moduleAssets = getString(R.string.module_feature_assets);
        moduleNative = getString(R.string.module_feature_native);
        final List<String> modules = Arrays.asList(moduleNative);

//        installManager.deferredInstall(modules).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(Exception e) {
//                toastAndLog(e.getMessage());
//            }
//        });
//        try {
//            installAllFeaturesDeferred();
//        }
//        catch(InterruptedException ex){
//            toastAndLog("Interrupted exception"+ex);
//        }
        if (!installManager.getInstalledModules().contains(moduleNative)) {
            installModule(moduleNative);
        }
        else {
            findViewById(R.id.btn_load_native).setEnabled(true);
        }
        //mProgress.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        installManager.registerListener(myListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        installManager.unregisterListener(myListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_load_java:
                //startQigsawInstaller(moduleJava);
                loadAndLaunchModule(moduleJava);
                //toastAndLog("hi");
                break;
            case R.id.btn_load_install:
                //startQigsawInstaller(moduleJava);
                launchActivity(INSTALL_SAMPLE_ACTIVITY);
                //toastAndLog("hi");
                break;
//            case R.id.btn_load_assets:
//                startQigsawInstaller(moduleAssets);
//                break;
            case R.id.btn_load_native:
                //launchDeferredModule(moduleNative);
//                try {
//                    installAllFeaturesDeferred();
//                }
//                catch(InterruptedException ex){
//                    toastAndLog("Interrupted exception"+ex);
//                }
                //loadAndLaunchModule(moduleNative);

               // launchActivity(INSTALL_SAMPLE_ACTIVITY);
                launchDeferredModule(moduleNative);
                break;
//            case R.id.btn_install_all_now:
//                installAllFeaturesNow();
//
//                break;
//            case R.id.btn_install_all_deferred:
//                long start1 = System.currentTimeMillis();
//                installAllFeaturesDeferred();
//                long end1 = System.currentTimeMillis();
//                toastAndLog("time taken " + (end1-start1));
//                break;
//            case R.id.btn_uninstall_all_deferred:
//                uninstallAllFeaturesDeferred();
//                break;
            default:
                break;
        }
    }

//    private void startQigsawInstaller(String moduleName) {
//        Intent intent = new Intent(this, QigsawInstaller.class);
//        ArrayList<String> moduleNames = new ArrayList<>();
//        moduleNames.add(moduleName);
//        intent.putStringArrayListExtra(QigsawInstaller.KEY_MODULE_NAMES, moduleNames);
//        startActivityForResult(intent, QigsawInstaller.INSTALL_REQUEST_CODE);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == QigsawInstaller.INSTALL_REQUEST_CODE) {
//            switch (resultCode) {
//                case RESULT_OK:
//                    if (data != null) {
//                        ArrayList<String> moduleNames = data.getStringArrayListExtra(QigsawInstaller.KEY_MODULE_NAMES);
//                        if (moduleNames != null && moduleNames.size() == 1) {
//                            loadAndLaunchModule(moduleNames.get(0));
//                        }
//                    }
//                    break;
////                case RESULT_CANCELED:
////                    break;
//                default:
//                    break;
//            }
//        }
//
//    }

    private void onSuccessfullyLoad(String moduleName, boolean launch) {
        if (launch) {
            if (moduleName.equals(moduleJava)) {
                launchActivity(JAVA_SAMPLE_ACTIVITY);
            } else if (moduleName.equals(moduleAssets)) {
                displayAssets();
            } else if (moduleName.equals(moduleNative)) {
                launchActivity(NATIVE_SAMPLE_ACTIVITY);
            }
        }
        displayButtons();
    }

    private void launchActivity(String className) {
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), className);
        startActivity(intent);
    }

    private void displayAssets() {
        try {
            Context context = createPackageContext(getPackageName(), 0);
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open("assets.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder stringBuffer = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                stringBuffer.append(str);
            }
            try {
                is.close();
                br.close();
            } catch (IOException ie) {
                //ignored
            }
            new AlertDialog.Builder(this)
                    .setTitle("Asset Content")
                    .setMessage(stringBuffer.toString())
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void installAllFeaturesNow() {
        final SplitInstallRequest request = SplitInstallRequest.newBuilder()
                .addModule(moduleJava)
                //.addModule(moduleNative)
                .addModule(moduleAssets)
                .build();
        installManager.startInstall(request).addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                toastAndLog("Loading " + request.getModuleNames());
            }
        });
    }

    private void installAllFeaturesDeferred() throws InterruptedException {

        final List<String> modules = Arrays.asList(moduleNative);

        installManager.deferredInstall(modules).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                toastAndLog(e.getMessage());
            }
        });
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(Exception e) {
//                        toastAndLog(e.getMessage());
//                        Log.d("deferred", "inside deferred install");
//                    }
//                });
        if (installManager.getInstalledModules().contains(moduleNative)) {
            updateProgressMessage("Already installed!");
            onSuccessfullyLoad(moduleNative, true);
            //toastAndLog("installed");
        }
        else {
//            int t=0;
//            while(!installManager.getInstalledModules().contains(moduleNative))
//            {
//
//                    t++;
//                   // toastAndLog("Time elapsed: " + t +" seconds..");
//                    Thread.sleep(1000);
//
//            }

            new Thread(new Runnable() {
                int x=0;
                @Override
                public void run() {
                    while (!installManager.getInstalledModules().contains(moduleNative)) {
                        try {
                            x++;
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                Log.d(TAG,"Time taken"+x+"seconds");

                            }
                        });

                    }
                    toastAndLog("Time elapsed"+x+"seconds");
                }
            }).start();
        }
    }

    private void uninstallAllFeaturesDeferred() {

        final List<String> modules = Arrays.asList(moduleJava, moduleAssets, moduleNative);

        installManager.deferredUninstall(modules)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        toastAndLog(e.getMessage());
                    }
                });
    }

    private void toastAndLog(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        Log.d(TAG, text);
    }
    private void launchDeferredModule(String name) {
        //updateProgressMessage("Loading module " + name);
        if (installManager.getInstalledModules().contains(name)) {
            updateProgressMessage("Already installed!!");
            //toastAndLog("Already installed");
            onSuccessfullyLoad(name, true);
            return;
        }
        else{
            toastAndLog("The module hasn't been installed yet");
        }

    }
    private static int mySessionId = 0;
    private static final String LOG_TAGG = "Anushka baseoffice";
    private void installModule(String name)
    {
//        SplitInstallStateUpdatedListener listener = state-> {
//
//            if (state.sessionId() == mySessionId) {
//                switch (state.status()) {
//                    case SplitInstallSessionStatus.DOWNLOADING:
//                        Log.d(LOG_TAGG,"Downloading");
//                    case SplitInstallSessionStatus.INSTALLED:
//                        Log.d(LOG_TAGG,"Installed");
//                    case SplitInstallSessionStatus.FAILED:
//                        Log.d(LOG_TAGG,"Failed");
//                }
//            }
//
//        };
        //installManager.registerListener(listener);
        SplitInstallRequest request = SplitInstallRequest.newBuilder().addModule(name).build();
        installManager.startInstall(request).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof SplitInstallException) {
                    int errorCode = ((SplitInstallException) e).getErrorCode();
                    switch (errorCode) {
                        case SplitInstallErrorCode.INCOMPATIBLE_WITH_EXISTING_SESSION:
                            toastAndLog(getString(R.string.installer_error_incompatible_with_existing_session));
                            break;
                        case SplitInstallErrorCode.SERVICE_DIED:
                            toastAndLog(getString(R.string.installer_error_service_died));
                            break;
                        case SplitInstallErrorCode.NETWORK_ERROR:
                            toastAndLog(getString(R.string.installer_error_network_error));
                            break;
                        case SplitInstallErrorCode.ACTIVE_SESSIONS_LIMIT_EXCEEDED:
                            toastAndLog("Please wait for the other download to complete");
                            break;
                        case SplitInstallErrorCode.INTERNAL_ERROR:
                            toastAndLog(getString(R.string.installer_error_internal_error));
                            break;
                        case SplitInstallErrorCode.SESSION_NOT_FOUND:
                            //ignored
                            break;
                        case SplitInstallErrorCode.INVALID_REQUEST:
                            toastAndLog(getString(R.string.installer_error_invalid_request));
                            break;
                        case SplitInstallErrorCode.API_NOT_AVAILABLE:
                            break;
                        case SplitInstallErrorCode.MODULE_UNAVAILABLE:
                            toastAndLog(getString(R.string.installer_error_module_unavailable));
                            break;
                        case SplitInstallErrorCode.ACCESS_DENIED:
                            toastAndLog(getString(R.string.installer_error_access_denied));
                            break;
                        default:
                            break;
                    }

                }
            }
        });
        //installManager.unregisterListener(listener);
    }
    private void loadAndLaunchModule(String name) {
        updateProgressMessage("Loading module " + name);
        if (installManager.getInstalledModules().contains(name)) {
            updateProgressMessage("Already installed!");
            //toastAndLog("Already installed");
            onSuccessfullyLoad(name, true);
            return;
        }
        int mySessionId=0;
        String LOG_TAGG="Anushka teststring";
//        SplitInstallStateUpdatedListener listener = state-> {
//            toastAndLog("install module before if condition");
//            if (state.sessionId() == mySessionId) {
//                switch (state.status()) {
//                    case SplitInstallSessionStatus.DOWNLOADING:
//                        toastAndLog("Downloading");
//                    case SplitInstallSessionStatus.INSTALLED:
//                        toastAndLog("Installed");
//                    case SplitInstallSessionStatus.FAILED:
//                        toastAndLog("Failed");
//                }
//            }
//
//        };
        SplitInstallRequest request = SplitInstallRequest.newBuilder().addModule(name).build();
        installManager.startInstall(request).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //toastAndLog("hi" + e.getMessage());
                if (e instanceof SplitInstallException) {
                    int errorCode = ((SplitInstallException) e).getErrorCode();
                    switch (errorCode) {
                        case SplitInstallErrorCode.INCOMPATIBLE_WITH_EXISTING_SESSION:
                            toastAndLog(getString(R.string.installer_error_incompatible_with_existing_session));
                            break;
                        case SplitInstallErrorCode.SERVICE_DIED:
                            toastAndLog(getString(R.string.installer_error_service_died));
                            break;
                        case SplitInstallErrorCode.NETWORK_ERROR:
                            toastAndLog(getString(R.string.installer_error_network_error));
                            break;
                        case SplitInstallErrorCode.ACTIVE_SESSIONS_LIMIT_EXCEEDED:
                            toastAndLog("Please wait for the other download to complete");
                            break;
                        case SplitInstallErrorCode.INTERNAL_ERROR:
                            toastAndLog(getString(R.string.installer_error_internal_error));
                            break;
                        case SplitInstallErrorCode.SESSION_NOT_FOUND:
                            //ignored
                            break;
                        case SplitInstallErrorCode.INVALID_REQUEST:
                            toastAndLog(getString(R.string.installer_error_invalid_request));
                            break;
                        case SplitInstallErrorCode.API_NOT_AVAILABLE:
                            break;
                        case SplitInstallErrorCode.MODULE_UNAVAILABLE:
                            toastAndLog(getString(R.string.installer_error_module_unavailable));
                            break;
                        case SplitInstallErrorCode.ACCESS_DENIED:
                            toastAndLog(getString(R.string.installer_error_access_denied));
                            break;
                        default:
                            break;
                    }

                }
            }
        });
//        }).addOnSuccessListener(new OnSuccessListener<Integer>() {
//            @Override
//            public void onSuccess(Integer integer) {
//                toastAndLog("Successfully installed");
//                //onSuccessfullyLoad(name,true);
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Integer>() {
//            @Override
//            public void onComplete(Task<Integer> task) {
////                Toast.makeText(MainActivity.this, "Module " + name +
////                        " installed", Toast.LENGTH_SHORT).show();
//               // onSuccessfullyLoad(name,true);
//                boolean k=installManager.getInstalledModules().contains(moduleJava);
//                //toastAndLog("Here"+k);
//            }
//        });
        updateProgressMessage("Starting install for " + name);
    }

    private void updateProgressMessage(String message) {
        if (progressbarGroups.getVisibility() != View.VISIBLE) {
            displayProgress();
        }
        mProgressText.setText(message);
    }

    private void displayButtons() {
        buttonGroups.setVisibility(View.VISIBLE);
        progressbarGroups.setVisibility(View.GONE);
    }

    private void displayProgress() {
        buttonGroups.setVisibility(View.VISIBLE);
        progressbarGroups.setVisibility(View.GONE);
    }
}
