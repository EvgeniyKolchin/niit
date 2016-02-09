package com.example.evgeniy.airpoooooort;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;


public class MyHttpClient {

    private static final String TAG = "MyHttpClient";
    private static final String SERVER = "http://85.143.221.58:8000/";
    private TextView tvContent;
    private ImageView image;

    public MyHttpClient() {
    }

    public void setTv(TextView tvContent) {
        this.tvContent = tvContent;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public void getRequest(String id) { // id - "major:minor" beacons
        AsyncHttpClient client = new AsyncHttpClient(); // создаём новый асинкхронный хттп клиент.
        client.get(SERVER + id, new AsyncHttpResponseHandler() { // отправляем гет запрос и получаем:

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) { // если запрос прошёл успешно: статус код, заголовки и тело.
                String allHeaders = "";
                for (Header header : headers)
                    allHeaders += header.getName() + " : " + header.getValue() + "\n"; // просто все хедеры в 1 переменну для логирования
                Bitmap bmp = BitmapFactory.decodeByteArray(response, 0, response.length); // вот тут мы и декодим байт массив в битмап
                image.setImageBitmap(bmp); // сетится картинка из битмапов и сразу выводиться на главные экран.
                tvContent.setText("statusCode: " + statusCode); // ставим статус код
                Log.d(TAG, "statusCode: " + statusCode + "\n"
                        + "header[] : " + allHeaders + "\n");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                tvContent.setText("statusCode: " + statusCode +"\n GET FAILURE"); // STUB
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

}
