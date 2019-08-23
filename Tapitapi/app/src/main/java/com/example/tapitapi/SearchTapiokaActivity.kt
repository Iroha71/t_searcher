package com.example.tapitapi

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.SimpleAdapter
import com.example.tapitapi.`interface`.TapiokaInterface
import com.example.tapitapi.`interface`.UploadInterface
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_search_tapioka.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.io.*
import kotlin.random.Random

class SearchTapiokaActivity : AppCompatActivity() {
    var siteUrl:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_tapioka)
    }

    override fun onResume() {
        super.onResume()
        menuList.visibility = View.INVISIBLE
        text.visibility=View.INVISIBLE
        webSearchButton.visibility=View.INVISIBLE
        webSearchButton.setOnClickListener(SendWebSite())
        var uri:Uri? = null
        try {
            val extras:Bundle? = intent.extras
            for(key:String in extras!!.keySet()){
                Log.i("<>",key)
                Log.i("===>",extras!!.get(key).toString())
            }
            uri = Uri.parse(intent.extras!!.get("android.intent.extra.TEXT").toString())
            var processUri:ArrayList<String> = uri.toString().split("?") as ArrayList<String>
            processUri[0] += "media?size=m"
            Log.i("===p",processUri[0])
            /* サムネイル表示処理 */
            Picasso.get()
                .load(processUri[0])
                .resize(500,500)
                .into(object: Target{
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        val rand = Random.nextInt(100)
                        val file = File(getExternalFilesDir(null),"image${rand}.png")
                        try{
                            Log.i("===",file.toString())
                            file.createNewFile()
                            val fileOutputStream = FileOutputStream(file)
                            bitmap!!.compress(Bitmap.CompressFormat.PNG,100,fileOutputStream)
                            fileOutputStream.flush()
                            fileOutputStream.close()

                            val eFile = File(getExternalFilesDir(null),"image${rand}.png")
                            val bm = BitmapFactory.decodeFile(eFile.path)
                            imageView2.setImageBitmap(bm)
                            val okHttpClient = OkHttpClient()
                            val path:String=eFile.path
                            val file2=File(path)
                            val rFile=RequestBody.create(MediaType.parse("multipart/form-data"),file2)
                            val body = MultipartBody.Part.createFormData("image",file.name,rFile)
                            val retrofit = Retrofit.Builder().let {
                                it.baseUrl("https://tapioka-searcher.yanai-k.com/")
                                it.client(okHttpClient)
                                it.addConverterFactory(GsonConverterFactory.create())
                                it.build()
                            }
                            var servise = retrofit.create(UploadInterface::class.java)
                            servise.upload(body).enqueue(CallBackImage())

                        }catch (e:java.lang.Exception){
                            Log.i("===",e.toString())
                        }
                    }

                    override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                    }
                })
        }catch (e:Exception){
            uri = null
            Log.i("===e",e.toString())
        }
    }
    /* 画像解析結果後の処理 */
    inner class CallBackImage:retrofit2.Callback<Res>{
        override fun onResponse(call: Call<Res>, response: Response<Res>) {

            Log.i("aaaa",response.body().toString())
            var store = "該当なし"
            when(response.body()!!.pick){
                "chunshuitang" ->{
                    store = "春水堂"
                }
                "mottram" ->{
                    store="モッチャム"
                }
                "tapiking"->{
                    store="タピキング"
                }
                "gongcha"->{
                    store="Gong cha"
                }
                "teaway" -> {
                    store = "TeaWay"
                }
            }
            storeName.text=store
            if(response.body()!!.status=="true"){
                val okHttpClient = OkHttpClient()
                val retrofit = Retrofit.Builder().let {
                    it.baseUrl("https://tapioka-searcher.yanai-k.com/")
                    it.client(okHttpClient)
                    it.addConverterFactory(GsonConverterFactory.create())
                    it.build()
                }
                val servise = retrofit.create(TapiokaInterface::class.java)
                servise.getMenu(response.body()!!.pick).enqueue(GetMenu())
            }else{
                storeName.text="該当なし"
            }
        }
        /* メニュー取得後の処理 */
        inner class GetMenu:retrofit2.Callback<Store>{
            override fun onResponse(call: Call<Store>, response: Response<Store>) {
                Log.i("aaa",response.body()!!.menu.toString())
                val adapter = SimpleAdapter(this@SearchTapiokaActivity,makeHash(response.body()!!.menu),
                    R.layout.menu_list_row, arrayOf("name","price"),intArrayOf(R.id.menuName,R.id.price))
                siteUrl=response.body()!!.url
                menuList.adapter=adapter
                menuList.visibility = View.VISIBLE
                text.visibility=View.VISIBLE
                webSearchButton.visibility=View.VISIBLE
            }
            override fun onFailure(call: Call<Store>, t: Throwable) {
                Log.i("aaa",t.toString())
            }

            private fun makeHash(menus:ArrayList<Menu>):ArrayList<HashMap<String,Any>>{
                var list:ArrayList<HashMap<String,Any>>? = ArrayList<HashMap<String,Any>>()
                for(menu in menus){
                    val hashMap=HashMap<String,Any>()
                    hashMap["name"] = menu.name
                    hashMap["price"]="￥"+menu.price
                    list!!.add(hashMap)
                }
                return list!!
            }
        }

        override fun onFailure(call: Call<Res>, t: Throwable) {
            Log.i("aaaa",t.toString())
        }
    }

    inner class SendWebSite:View.OnClickListener{
        override fun onClick(p0: View?) {
            if(siteUrl!=null){
                var intent= Intent(this@SearchTapiokaActivity,WebActivity::class.java)
                intent.putExtra("url",siteUrl)
                startActivity(intent)
            }
        }
    }
}
