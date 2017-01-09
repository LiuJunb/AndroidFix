# Android 热修复框架 AndFix ( 二 )
Android 热修复框架，AndFix的使用教程。该项目将会教大家如何一步一步去使用阿里里巴巴的热修复框架AndFix

这篇文章将演示一下在项目中如何使用AndFix框架，如何实现动态修复app中的bugs。在看这篇文章之前请先看[Android 热修复框架 AndFix (一)](http://blog.csdn.net/u012987546/article/details/53583417),想了解更多可以看:[Android 热修复框架 AndFix ( 三 )](http://blog.csdn.net/u012987546/article/details/53710712)

[AndroidFix项目github地址 ](https://github.com/LiuJunb/AndroidFix)

情景分析：

1.假如一个登录界面，由于一个小小的问题导致点登录就蹦了！当发现这个bug的时候，app已经发布上线了。

2.此时，我们可以通过热修复完成这个bug ,而不需重新发布一个新app的版本。

## 1.新建一个项目



**1.集成AndFix**

为app下的gradle添加依赖，添加依赖有构建一下项目

```
dependencies {
    compile 'com.alipay.euler:andfix:0.3.1@aar'
}
```

**2.新建Application的子类MainApplication**

在onCreate()方法中进行下面的操作：

1.初始化PatchManager

```
  mPatchManager = new PatchManager(this);
  mPatchManager.init("1.0");// 1.0是应用的版本号
```



2.加载补丁

```
mPatchManager.loadPatch();
```



3.添加补丁文件

（这里为了演示简单补丁文件只是从内存中获取，而不从网上获取，一般的情况是从服务器上下载）

```
mPatchManager.addPatch(patchFileString);
```



```

/**
 * Description :
 * Author : liujun
 * Email  : liujin2son@163.com
 * Date   : 2016/12/11 0011
 */
public class MainApplication extends Application {
    private PatchManager mPatchManager;
    private static final String TAG = "MainApplication";
    private static final String APATCH_PATH = "/out.apatch";

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize
        mPatchManager = new PatchManager(this);
        mPatchManager.init("1.0");
        Log.d(TAG, "inited.");


        // load patch
        mPatchManager.loadPatch();
        Log.d(TAG, "apatch loaded.");


        // add patch at runtime
        try {
            // .apatch file path
            String patchFileString = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + APATCH_PATH;
            //   /storage/sdcard/out.apatch  系统自带模拟器的路径
            //   /mnt/sdcard/out.apatch  genymotion的路径
            mPatchManager.addPatch(patchFileString);
            Log.d(TAG, "apatch:" + patchFileString + " added.");
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }

    }
}

```



**3.配置MainApplication**

```

    <application
        ....
        android:name=".MainApplication"
        android:theme="@style/AppTheme" >
        <activity android:name=".MainActivity">
           ...
        </activity>
    </application>
```



**4.添加权限**

```
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
```



**5.完成登录布局**

布局中有：一张头像，输入用户名，输入密码，点击登录

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    >

    <ImageView
        android:src="@mipmap/icon"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:layout_gravity="center"
        android:layout_marginTop="20dp"
        android:text="Hello World!" />

    <EditText
        android:id="@+id/edit_username"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:layout_marginLeft="10dp"
        android:layout_marginRight="10dp"
        android:layout_marginTop="5dp"
        android:hint="输入用户名"
        />

    <EditText
        android:id="@+id/edit_password"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:layout_marginLeft="10dp"
        android:layout_marginRight="10dp"
        android:layout_marginTop="5dp"
        android:hint="输入密码"
        />

    <Button
        android:id="@+id/btn_login"
        android:layout_width="match_parent"
        android:layout_height="60dp"
        android:layout_marginLeft="10dp"
        android:layout_marginRight="10dp"
        android:layout_marginTop="5dp"
        android:text="登录"
        />
</LinearLayout>

```



**6.完成MianActivity**

在MianActivity中实例化所有的控件，然后给`登录`添加一个点击事件。

当点登录的时候，app就会蹦，因为username=null ； password=null； 

这里是故意制造bugs

```

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText edit_username,edit_password;
    private Button btn_login;
    private String username=null,password=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_password = (EditText) findViewById(R.id.edit_password);
        btn_login = (Button) findViewById(R.id.btn_login);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				//代码执行到这里会蹦，因为username=null ； password=null；
                if(password.equals("123")&&username.equals("liujun"))
                    Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                else{
                    Toast.makeText(MainActivity.this,"用户名与密码出错",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //为了应用重新启动，再次执行MainApplication中onCreate方法中的代码
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    
}

```



**7.生成签名文件**

1.点击Android studio中的: Buider->Generate Signed APk..-->Create New

 ![1](https://raw.githubusercontent.com/LiuJunb/AndroidFix/master/screen/1.png)



2.最后生成一个签名文件：`androidfix.jks`

**8.签名打包APP**

1.点击Android studio中的: Buider->Generate Signed APk..-->Next

 ![2](https://github.com/LiuJunb/AndroidFix/blob/master/screen/2.png?raw=true)

2.签名打包后就会生成一个正式签名的apk文件：`app-release1.0.apk`

**9.发布运行出现bugs**

1.在点击登录的时候蹦了

 ![12](https://github.com/LiuJunb/AndroidFix/blob/master/screen/12.gif?raw=true)

**10.开始修复bugs**

1.修改MainActivity中的点击登录里面的逻辑

```
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private EditText edit_username,edit_password;
    private Button btn_login;
    private String username=null,password=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_password = (EditText) findViewById(R.id.edit_password);
        btn_login = (Button) findViewById(R.id.btn_login);



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 修复bug的代码
                 */
                username = edit_username.getText().toString().trim();
                password = edit_password.getText().toString().trim();
                /**
                 * 修复bug的代码
                 */
                if(username.equals("")||password.equals("")){
                    Toast.makeText(MainActivity.this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
                    return ;
                }

                if(password.equals("123")&&username.equals("liujun"))
                    Toast.makeText(MainActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                else{
                    Toast.makeText(MainActivity.this,"用户名与密码出错",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //为了应用重新启动，再次执行MainApplication中onCreate方法中的代码
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
```



**11.签名打包APP**

 重复第8步

1.1.点击Android studio中的: Buider->Generate Signed APk..-->Next

2.生成一个修复bugs后的正式的apk:`app-release1.0.fix.apk`

**12.制作补丁**

1.下载制作补丁工具:[apkpatch-1.0.3.zip](https://github.com/alibaba/AndFix/raw/master/tools/apkpatch-1.0.3.zip)

2.解压：制作补丁的工具:

3.把androidfix.jks , app-release1.0.apk与app-release1.0.fix.apk拷贝到刚解压补丁工具的文件夹中

4.启动DOS命令行，进入到补丁文件工具文件夹，执行：

```
apkpatch -f C:\xxxx\app-release1.0.fix.apk -t C:\xxx\app-release1.0.apk -o D:\apk -k C:\xxxx\androidfix.jks -p xxxxxx -a xxxxxxx -e xxxxxxx

//参数说明：
 -a,--alias <alias>     keystore entry alias.
 -e,--epassword <***>   keystore entry password.
 -f,--from <loc>        new Apk file path.
 -k,--keystore <loc>    keystore path.
 -n,--name <name>       patch name.
 -o,--out <dir>         output dir.
 -p,--kpassword <***>   keystore password.
 -t,--to <loc>          old Apk file path.
```

5.执行了上面的命令就会在D:\apk文中生成下面三个文件：

```
smali
app-release1-44c095be1acbdd01beed3afd478182f0.apatch
diff.dex
```

其中：app-release1-44c095be1acbdd01beed3afd478182f0.apatch是补丁文件

把这个补丁文件修改文件名为：`out.apatch`，方便使用。

**13.把补丁push到手机的内存中**

1.这里没有把补丁`out.apatch`放到服务器上去给客户端下载，而是直接push到手机上，目的方便测试

2.因为这里使用的模拟器是genymotion：把补丁文件push到下面文件夹；

![3](https://github.com/LiuJunb/AndroidFix/blob/master/screen/3.png?raw=true)

**14.从新启动APP**

1.从新启动APP  ,  APP在启动的时候会在sdcard下面加载补丁文件，如果补丁文件存在，就会自动添加补丁，这些代码在在发布app-release1.0.ap的时候已经写好。

2.重新启动后bugs已被修复，并不需要重新安装APP

 ![13](https://github.com/LiuJunb/AndroidFix/blob/master/screen/13.gif?raw=true)

3.至此APP登录出现的bug修复完成







