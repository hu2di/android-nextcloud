package com.blogspot.hu2di.nextcloud;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.UploadRemoteFileOperation;

import java.io.File;

public class MainActivity extends AppCompatActivity implements OnDatatransferProgressListener, OnRemoteOperationListener {

    private TextView tvLogin;
    private Button btnUpload;

    private OwnCloudClient mClient;
    private Handler mHandler = new Handler();

    public static final String SEVER_BASE_URL = "your-server-url";
    public static final String USERNAME = "your-user-name";
    public static final String PASSWORD = "your-password";

    public static final String PATH_FILE = "your-path-file"; //ex: "/sdcard/xyz.png"

    private String TAG = "void";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initLogin();
    }

    private void initView() {
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile(PATH_FILE);
            }
        });
    }

    private void initLogin() {
        mHandler = new Handler();

        // Parse URI to the base URL of the Nextcloud server
        Uri serverUri = Uri.parse(SEVER_BASE_URL);

        // Create client object to perform remote operations
        mClient = OwnCloudClientFactory.createOwnCloudClient(
                serverUri,
                this,
                // Activity or Service context
                true);

        // Set basic credentials
        mClient.setCredentials(
                OwnCloudCredentialsFactory.newBasicCredentials(USERNAME, PASSWORD)
        );
    }

    private void uploadFile(String pathFileToUp) {
        File fileToUpload = new File(pathFileToUp);
        if (fileToUpload.exists()) {
            String remotePath = "/" + fileToUpload.getName();
            String mimeType = "image/png";
            startUpload(fileToUpload, remotePath, mimeType);
        }
    }

    private void startUpload(File fileToUpload, String remotePath, String mimeType) {
        UploadRemoteFileOperation uploadOperation = new UploadRemoteFileOperation(fileToUpload.getAbsolutePath(), remotePath, mimeType);
        uploadOperation.addDatatransferProgressListener(this);
        uploadOperation.execute(mClient, this, mHandler);
    }

    @Override
    public void onRemoteOperationFinish(RemoteOperation operation, RemoteOperationResult result) {
        if (operation instanceof UploadRemoteFileOperation) {
            if (result.isSuccess()) {
                // do your stuff here
                Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                tvLogin.setText("SUCCESS");
            }
        }
    }

    @Override
    public void onTransferProgress(long progressRate, long totalTransferredSoFar, long totalToTransfer, String fileName) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // do your UI updates about progress here
                Log.d(TAG, "onProgress");
            }
        });
    }
}
