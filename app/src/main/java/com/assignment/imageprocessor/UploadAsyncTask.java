package com.assignment.imageprocessor;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class UploadAsyncTask extends AsyncTask<String, Void, Void> {

    private static final   String TAG=UploadAsyncTask.class.getSimpleName();

    private HttpMethod httpMethod;
    private String uploadUri;
    private Uri fileUri;
    private NetworkListener networkListener;
    private String loadMessage="Please wait...";
    private ProgressDialog progressDialog;
    private Context context;

    public UploadAsyncTask(Context context,String uploadUri,
                           Uri fileUri,
                           NetworkListener networkListener){

        this.context=context;
        this.uploadUri=uploadUri;
        this.fileUri=fileUri;
        this.networkListener=networkListener;
        this.httpMethod=HttpMethod.POST;

    }
    @Override
    protected Void doInBackground(String... params) {

        ResponseEntity<?> responseEntity = null;
        try {
            Log.d(TAG, "Posting data " + fileUri);

            HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Connection", "Close");
            MultiValueMap<String, Object> parts =
                    new LinkedMultiValueMap<>();

            System.out.println("File to upload : " + fileUri);

            Resource resource=null;
            if(fileUri.toString().startsWith("content:")) {

                InputStream inputStream = context.getContentResolver().openInputStream(fileUri);

                File file = File.createTempFile("tempImg", null, context.getCacheDir());
                OutputStream outputStream = new FileOutputStream(file);
                IOUtils.copy(inputStream, outputStream);
                resource=new FileSystemResource(file.getAbsoluteFile());
            }
            else{
                resource=new FileSystemResource(fileUri.getPath());
            }

            parts.add("imageName",resource);
            HttpEntity<?> entity = new HttpEntity<Object>(parts,headers);

            RestTemplate restTemplate = new RestTemplate();

            List<HttpMessageConverter<?>> list = new ArrayList<HttpMessageConverter<?>>();
            list.add(new ByteArrayHttpMessageConverter());
            list.add(new ResourceHttpMessageConverter());
            list.add(new FormHttpMessageConverter());
            restTemplate.setMessageConverters(list);


            responseEntity = restTemplate.exchange(uploadUri,httpMethod,entity,String.class);
            Log.d(TAG,"responseEntity "+responseEntity.getBody());


            if(progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();

            if (responseEntity.getStatusCode()==HttpStatus.OK||
                    responseEntity.getStatusCode() == HttpStatus.CREATED) {

                if(networkListener!=null)
                    networkListener.onSuccess(responseEntity.getBody());
            }
            else
            {
                if(networkListener!=null)
                    networkListener.onFailure(responseEntity.getBody());
            }
        } catch (HttpClientErrorException e) {
            /**
             *
             * If we get a HTTP Exception display the error message
             */
            e.printStackTrace();


            if(progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();

            if(networkListener!=null)
                networkListener.onFailure(e.getMessage());

        }
        catch(RestClientException e){

            e.printStackTrace();

            if(progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();

            if(networkListener!=null)
                networkListener.onFailure(e.getMessage());

        }
        catch (Exception e) {

            e.printStackTrace();

            if(progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();

            if(networkListener!=null)
                networkListener.onFailure(e.getMessage());

        }
    return null;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();

        progressDialog=new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(loadMessage);
        progressDialog.show();

    }

    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        if(progressDialog!=null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


}
