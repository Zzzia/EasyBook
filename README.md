### Java/Android小说爬虫工具

[![](https://jitpack.io/v/Zzzia/EasyBook.svg)](https://jitpack.io/#Zzzia/EasyBook)


使用简单的几行代码，打造你自己的小说开源软件，自定义书源，多站点解析，并发搜索下载。


[App体验](https://github.com/Zzzia/Book)

QQ交流群：29527219

<img src="https://github.com/Zzzia/Book/blob/master/screenshot/1.png" width="280"><img src="https://github.com/Zzzia/Book/blob/master/screenshot/2.png" width="280"><img src="https://github.com/Zzzia/Book/blob/master/screenshot/3.png" width="280">

#### Android平台
**Step 1**. Add the JitPack repository to your build file

~~~gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
~~~

**Step 2**. Add the dependency

~~~java
dependencies {
    implementation 'com.github.Zzzia:EasyBook:2.58'
    implementation 'com.squareup.okhttp3:okhttp:4.2.2'
    implementation 'com.google.code.gson:gson:2.8.5'
}
~~~

**混淆(默认自动加入)**
~~~
-keep class com.zia.bookdownloader.bean.** { *; }
~~~


#### 使用说明：（具体可参照项目内简单示例或我写的[小说神器](https://github.com/Zzzia/Book)）

搜索：（Android会自动切换到主线程）

~~~java
EasyBook.search("天行")
        .subscribe(new StepSubscriber<List<Book>>() {
            @Override
            public void onFinish(@NonNull List<Book> books) {
                //所有站点小说爬取完后调用这个方法，传入所有站点解析的有序结果
            }

            @Override
            public void onError(@NonNull Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onMessage(@NonNull String message) {
                //一些搜索中的进度消息，错误原因等，可以用toast弹出
            }

            @Override
            public void onProgress(int progress) {
                //搜索进度
            }

            @Override
            public void onPart(@NonNull List<Book> books) {
                //某一个站点的小说搜索结果
            }
        });
~~~

加载目录：

~~~java
EasyBook.getCatalog(book)
        .subscribe(new Subscriber<List<Catalog>>() {
            @Override
            public void onFinish(List<Catalog> catalogs) {
                //加载结果，返回该书籍所有目录
            }
            //...
        });
~~~

加载某一章节内容：

~~~java
EasyBook.getContent(book,catalog)
        .subscribe(new Subscriber<List<String>>() {
            @Override
            public void onFinish(List<String> strings) {
                //返回该章节所有内容，按行保存在集合内，需要自行调整格式
            }
            //...
        });
~~~

下载书籍：

~~~java
EasyBook.download(book)
		.setSavePath("/sdcard/book")//设置保存的路径
		.setThreadCount(150)//设置下载的线程数
		.setType(Type.EPUB)//设置下载格式，如epub或txt
		.subscribe(new Subscriber<File>() {
			@Override
			public void onFinish(File file) {
				//下载完成后的文件
			}
			//...
		});
~~~

分段下载：

~~~java
EasyBook.downloadPart(book, 0, 100)
        .setThreadCount(50)
        .subscribe(new Subscriber<ArrayList<Chapter>>() {
            @Override
            public void onFinish(@NonNull ArrayList<Chapter> chapters) {
                //返回一个Chapter集合，保证不为空且都有contents
            }
            //...
        });
~~~

当然也支持同步调用，但不建议使用：

~~~java
List<Catalog> list = EasyBook.getCatalog(book).getSync();
~~~

销毁线程（解决内存泄漏）：

~~~java
Disposable disposable;

void onCreate(){
    disposable = EasyBook.search("天行").subscribe(...);
}

void onDestroy(){
    disposable.dispose();
}
~~~

添加自己的站点解析：

[教程](CustomRule.md)

~~~java
//添加一个自己解析的站点类，叫Zhuishushenqi，需要继承Site
SiteCollection.getInstance().addSite(Zhuishushenqi());
~~~

解析起点排行榜：

~~~java
EasyBook.getHottestRank().subscribe();
~~~

解析起点分类排行榜：

~~~java
EasyBook.getRank(rankInfo);
~~~

添加json站点解析 json格式如[zzzia源](http://zzzia.net/easybook.json) 
详细说明如[XpathSiteRule](https://github.com/Zzzia/EasyBook/blob/master/easybookmodule/src/main/java/com/zia/easybookmodule/bean/rule/XpathSiteRule.java)
~~~kotlin
val json = getJson()
val rules = Gson().fromJson<List<XpathSiteRule>>(
    json, TypeToken.getParameterized(List::class.java, XpathSiteRule::class.java).type
)
val sites = ArrayList<Site>()
rules.forEach {
      sites.add(CustomXpathSite(it))
}
SiteCollection.getInstance().addSites(sites)
~~~



~~~
v2.59
将生成的epub指定为utf-8格式

v2.58
修复url merge规则bug
提高自定义书源目录解析速度

v2.57
修复了书源并部分转移至在线书源
修复了一些在线书源的小问题
添加了在线书源制作教程

v2.55
删除/增加了几个书源
修复在线解析规则不能去广告bug
暴露网络接口供自定义实现

v2.50
修复自定义书源问题
删除了dom4j依赖

v2.49
增加了自定义json站点规则，基于xpath

v2.48
增加了更多解析内容，能够解析小说简介了，同时保证了图片等内容的完整性
在调用目录解析后会自动把内容更新在book里

v2.47
完善demo的分段搜索，升级至Androidx
提高了爬虫稳定性

v2.46
支持了分段搜索，以站点为单位陆续返回结果
为所有解析添加了同步方法，但不建议在含有并发的操作中使用
提高了爬虫稳定性

v2.45
更新了分段下载，将并发下载逻辑单独封装，便于使用

v2.44
添加对起点中文网的排行解析，用于书城功能

v2.42
尝试添加热修复版本控制

v2.40
添加EPUB封面，更换为xhtml格式，支持更多阅读软件
~~~

# Thanks

* okHttp
* epublib
* jsoup