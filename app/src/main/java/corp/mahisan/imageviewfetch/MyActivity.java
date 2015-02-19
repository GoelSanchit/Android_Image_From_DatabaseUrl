package corp.mahisan.imageviewfetch;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MyActivity extends Activity {
    ProgressDialog progress;
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        progress = new ProgressDialog(MyActivity.this);
        progress.setMessage("Fetching Data ");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.show();
        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        final ListView lstView1 = (ListView)findViewById(R.id.list);


        /** JSON from URL
         * [
         * {"ImageID":"1","ImageDesc":"My Sea View 1","ImagePath":"http://www.thaicreate.com/android/pic_a.png"},
         * {"ImageID":"2","ImageDesc":"My Sea View 2","ImagePath":"http://www.thaicreate.com/android/pic_b.png"},
         * {"ImageID":"3","ImageDesc":"My Sea View 3","ImagePath":"http://www.thaicreate.com/android/pic_c.png"},
         * {"ImageID":"4","ImageDesc":"My Sea View 4","ImagePath":"http://www.thaicreate.com/android/pic_d.png"}
         * ]
         */



        String url = "http://sanchitgoel.net78.net/imagefetch.php";

        try {
            JSONArray data = new JSONArray(getJSONUrl(url));

            final ArrayList<HashMap<String, String>> MyArrList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;

            for(int i = 0; i < data.length(); i++){
                JSONObject c = data.getJSONObject(i);
                map = new HashMap<String, String>();
                map.put("ImageID", c.getString("ImageID"));
                map.put("ImageDesc", c.getString("ImageDesc"));
                map.put("ImagePath", c.getString("ImagePath"));
                MyArrList.add(map);
            }

            lstView1.setAdapter(new ImageAdapter(this,MyArrList));
            progress.dismiss();
            final AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
            final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

            // OnClick
            lstView1.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {

                    View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
                            (ViewGroup) findViewById(R.id.layout_root));
                    ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
                    String sName = MyArrList.get(position).get("ImageDesc")

                            .toString();


                    try
                    {
                        image.setImageBitmap(loadBitmap(MyArrList.get(position).get("ImagePath")));
                    } catch (Exception e) {
                        // When Error
                        image.setImageResource(android.R.drawable.ic_menu_report_image);
                    }

                    imageDialog.setIcon(android.R.drawable.btn_star_big_on);
                    imageDialog.setTitle("Desc : " + MyArrList.get(position).get("ImageID"));
                    imageDialog.setView(layout);
                    imageDialog.setMessage(sName);
                    imageDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }

                    });

                    imageDialog.create();
                    imageDialog.show();

                }
            });



        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    public class ImageAdapter extends BaseAdapter
    {
        private Context context;
        private ArrayList<HashMap<String, String>> MyArr = new ArrayList<HashMap<String, String>>();

        public ImageAdapter(Context c, ArrayList<HashMap<String, String>> list)
        {
            // TODO Auto-generated method stub
            context = c;
            MyArr = list;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return MyArr.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            if (convertView == null) {
                convertView = inflater.inflate(R.layout.feed_item, null);
            }

            // ColImage
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView2);
            imageView.getLayoutParams().height = 100;
            imageView.getLayoutParams().width = 100;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            try
            {
                imageView.setImageBitmap(loadBitmap(MyArr.get(position).get("ImagePath")));
            } catch (Exception e) {
                // When Error
                imageView.setImageResource(android.R.drawable.ic_menu_report_image);
            }

            // ColPosition
            TextView txtPosition = (TextView) convertView.findViewById(R.id.title);
            txtPosition.setPadding(10, 0, 0, 0);
            txtPosition.setText("Title : " + MyArr.get(position).get("ImageID"));

            // ColPicname
            TextView txtPicName = (TextView) convertView.findViewById(R.id.desc);
            txtPicName.setPadding(50, 0, 0, 0);
            txtPicName.setText("Desc : " + MyArr.get(position).get("ImageDesc"));



            return convertView;

        }

    }

    /*** Get JSON Code from URL ***/
    public String getJSONUrl(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download file..");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }


    /***** Get Image Resource from URL (Start) *****/
    private static final String TAG = "ERROR";
    private static final int IO_BUFFER_SIZE = 4 * 1024;
    public static Bitmap loadBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;

        try {
            in = new BufferedInputStream(new URL(url).openStream(), IO_BUFFER_SIZE);

            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
            copy(in, out);
            out.flush();

            final byte[] data = dataStream.toByteArray();
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inSampleSize = 1;

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
        } catch (IOException e) {
            Log.e(TAG, "Could not load Bitmap from: " + url);
        } finally {
            closeStream(in);
            closeStream(out);
        }

        return bitmap;
    }

    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                android.util.Log.e(TAG, "Could not close stream", e);
            }
        }
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }
    /***** Get Image Resource from URL (End) *****/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

}
