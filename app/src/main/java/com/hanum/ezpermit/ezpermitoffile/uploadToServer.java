package com.hanum.ezpermit.ezpermitoffile;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.hanum.ezpermit.ezpermitoffile.MainActivity.getContext;

public class uploadToServer extends AsyncTask<String, Void, String> {


    String upLoadServerUri = "https://easypermit.net/assets/views/phppages/alluploader.php";

    /**********  File Path *************/
    final String uploadFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getClass().getPackage().getName() + "/files/";
    final String uploadFileName = "";
    int serverResponseCode = 0;

    File file[];




    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

//            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + "" + uploadFileName);

            Log.e("uploadFile", "Source File not exist :"
                    + uploadFilePath + "" + uploadFileName);
//
//            ((MainActivity) getContext()).runOnUiThread(new Runnable() {
//                @Override public void run() {
//                    Log.e("uploadFile", "Source File not exist :"
//                            + uploadFilePath + "" + uploadFileName);
//                   }
//            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                        dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){


                    Log.d("status", "File Upload Complete." + fileName);

//                    ((Activity) getContext()).runOnUiThread(new Runnable() {
//                        public void run() {
//
//                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
//                                    +" http://www.androidexample.com/media/uploads/"
//                                    +uploadFileName;
//
//                            Log.d("status", "File Upload Complete." + msg);
//                        }
//                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                Log.d("status", "MalformedURLException Exception : check script url." + ex);
                ex.printStackTrace();



//                ((Activity) getContext()).runOnUiThread(new Runnable() {
//                    public void run() {
//                        Log.d("status", "MalformedURLException Exception : check script url.");
//                    }
//                });

                Log.d("status", "MalformedURLException Exception : check script url.");

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                Log.d("status", "Got Exception : see logcat " + e);

                e.printStackTrace();

//                ((Activity) getContext()).runOnUiThread(new Runnable() {
//                    public void run() {
//                        Log.d("status", "Got Exception : see logcat ");
//                    }
//                });

                Log.d("status", "Got Exception : see logcat ");

                Log.e("Upload", "Exception : "
                        + e.getMessage(), e);
            }
            return serverResponseCode;

        } // End else block
    }

    @Override
    protected String doInBackground(String... strings) {


            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + getClass().getPackage().getName() + "/files/";
            Log.d("Files", "Path: " + path);
//            File f = new File(path);
//            File file[] = f.listFiles();

        Filewalker fw = new Filewalker();
        fw.walk(new File(path));
        return null;
    }

    public class Filewalker {

        public void walk(File root) {

            File[] list = root.listFiles();

            for (File f : list) {
                if (f.isDirectory()) {
                    Log.d("wwwwwwwww", "Dir: " + f.getAbsoluteFile());
                    walk(f);
                }
                else {
                    Log.d("wwwwwwwwww", "File: " + f.getAbsoluteFile());

                    for (File aFile : list) {
                        Log.d("Files", "FileName1111111:" + uploadFilePath + "" + aFile.getName());
                        uploadFile(String.valueOf(f.getAbsoluteFile()));
                    }
                }
            }


            Log.d("FIles" , "files" + list);

            if(list != null) {
                Log.d("Files", "Size: " + list);
                for (File aFile : list) {
                    Log.d("Files", "FileName1111111:" + uploadFilePath + "" + aFile.getName());
                    uploadFile(uploadFilePath + "" + aFile.getName());
                }


            }



        }
    }
}
