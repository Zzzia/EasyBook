### Java/Android小说爬虫工具

[![](https://jitpack.io/v/Zzzia/EasyBook.svg)](https://jitpack.io/#Zzzia/EasyBook)


使用简单的几行代码，打造你自己的小说开源软件，多站点解析，并发搜索下载。


[App体验](https://github.com/Zzzia/Book)

QQ群交流：29527219

<img src="https://github.com/Zzzia/EasyBook/blob/master/screenshot/1.jpg" width="280"><img src="https://github.com/Zzzia/EasyBook/blob/master/screenshot/2.jpg" width="280"><img src="https://github.com/Zzzia/EasyBook/blob/master/screenshot/3.jpg" width="280">

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
	implementation 'com.squareup.okhttp3:okhttp:3.11.0'
    implementation 'com.github.Zzzia:EasyBook:2.4'
}
~~~


#### IDEA平台

将release内的压缩包下载下来，解压后依次添加依赖，一共有7个jar。

---

#### 使用说明：（具体可参照项目内简单示例或我写的[小说神器](https://github.com/Zzzia/Book)）

搜索：（Android会自动切换到主线程）

~~~java
EasyBook.search("天行")
        .subscribe(new Subscriber<List<Book>>() {
            @Override
            public void onFinish(List<Book> books) {
                //搜索结果，返回book集合，提示用户选择
                //recyclerviewAdapter.load(books);
            }

            @Override
            public void onError(Exception e) {
                //搜索时遇到错误
            }

            @Override
            public void onMessage(String s) {
                //搜索的提示，如"正在搜索x趣阁"
            }

            @Override
            public void onProgress(int i) {
            //搜索进度，0 ~ 100
            }});
~~~

加载目录：

~~~java
EasyBook.getCatalog(book)
        .subscribe(new Subscriber<List<Catalog>>() {
            @Override
            public void onFinish(List<Catalog> catalogs) {
                //加载结果，返回该书籍所有目录
            }
            ...
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
            ...
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
			...
		});
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



~~~
v2.43
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