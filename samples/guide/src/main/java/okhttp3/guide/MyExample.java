package okhttp3.guide;

import okhttp3.*;
import okio.BufferedSink;

import java.io.File;
import java.io.IOException;

public class MyExample {

  public static void httpGetTest(){
    try {
      String strUrl = "https://raw.github.com/square/okhttp/master/README.md";
      OkHttpClient client = new OkHttpClient();
      Request request = new Request.Builder().url(strUrl).build();
      Response response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        String strResponse = response.body().string();
        System.out.println(strResponse);
      }
    }
    catch (IOException ex ){
      ex.printStackTrace();
    }
  }

  public static void syncGetTest()
  {
    final OkHttpClient client = new OkHttpClient();
    try {
      Request request = new Request.Builder()
            .url("http://publicobject.com/helloworld.txt")
            .build();

      Response response = client.newCall(request).execute();
      if( !response.isSuccessful())
        throw new IOException("Unexpected code" + response);

      Headers responeHeaders = response.headers();
      for( int i = 0; i < responeHeaders.size(); ++i )
      {
        System.out.println(responeHeaders.name(i) + ": " + responeHeaders.value(i));
      }
      System.out.println(response.body().string());
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
    }

  }

  public static void asyncGetTest()
  {
      OkHttpClient client = new OkHttpClient();
      Request request = new Request.Builder().url("http://publicobject.com/helloworld.txt")
              .build();
      client.newCall( request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
          e.printStackTrace();
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
          if( !response.isSuccessful())
            throw new IOException("Unexpected code " + response);
          Headers responseHeaders = response.headers();
          for( int i = 0; i < responseHeaders.size(); ++i ){
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
          }
          System.out.println(response.body().string());
        }
      });
      return;
  }

  public static void getResponseHeader()
  {
    OkHttpClient client = new OkHttpClient();
    try{
      Request request = new Request.Builder()
              .url("https://api.github.com/repos/square/okhttp/issues")
              .header("User-Agent", "OkHttp Headers.java")
              .addHeader("Accept", "application/json; q=0.5")
              .addHeader("Accept", "application/vnd.github.v3+json")
              .build();
      Response response = client.newCall(request).execute();
      if( !response.isSuccessful() )
        throw new IOException("Unexpected code " + response);

      System.out.println("Server: " + response.header("Server"));
      System.out.println("Date: " + response.header("Date"));
      System.out.println("Vary: " + response.headers("Vary"));
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }

  public static void postStringTest(){
    MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    try{
      String postBody = ""
              + "Releases\n"
              + "--------\n"
              + "\n"
              + " * _1.0_ May 6, 2013\n"
              + " * _1.1_ June 15, 2013\n"
              + " * _1.2_ August 11, 2013\n";

      Request request = new Request.Builder()
              .url("https://api.github.com/markdown/raw")
              .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, postBody))
              .build();

      Response response = client.newCall(request).execute();
      if( !response.isSuccessful())
        throw new IOException("Unexpected code " + response);
      System.out.println(response.body().string());
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }

  public static void postStreamTest()
  {
    final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    try{
      RequestBody requestBody = new RequestBody() {
        @Override
        public MediaType contentType() {
          return MEDIA_TYPE_MARKDOWN;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
          sink.writeUtf8("Numbers\n");
          sink.writeUtf8("-------\n");
          for (int i = 2; i <= 997; i++) {
            sink.writeUtf8(String.format(" * %s = %s\n", i, factor(i)));
          }
        }
        private String factor(int n) {
          for (int i = 2; i < n; i++) {
            int x = n / i;
            if (x * i == n) return factor(x) + " × " + i;
          }
          return Integer.toString(n);
        }
      };
      Request request = new Request.Builder()
              .url("https://api.github.com/markdown/raw")
              .post(requestBody)
              .build();
      Response response = client.newCall(request).execute();
      if(!response.isSuccessful())
        throw new IOException("Unexpected code " + response);
      System.out.println(response.body().string());
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }

  public static void postFileTest()
  {
    final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    File file = new File("README.md");
    Request request = new Request.Builder()
            .url("https://api.github.com/markdown/raw")
            .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
            .build();
    try {
      Response response = client.newCall(request).execute();
      if(!response.isSuccessful())
        throw new IOException("Unexpected code " + response);
      System.out.println(response.body().string());
    }catch (Exception ex){
      ex.printStackTrace();
    }

  }

  public static void main(String[] args) throws IOException {
    MyExample.postFileTest();
  }
}
