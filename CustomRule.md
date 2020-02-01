[XpathSiteRule.java](https://github.com/Zzzia/EasyBook/blob/master/easybookmodule/src/main/java/com/zia/easybookmodule/bean/rule/XpathSiteRule.java) 序列化书源json的类

[参考书源](https://github.com/Zzzia/EasyBook/blob/master/easybook.json)

假设网站是https://www.jx.la/ ，搜索斗破苍穹

### 1. 填写siteName和baseUrl

在`siteName`中填入笔趣阁jx，将向用户展示这个名字

在`baseUrl`中填入 https://www.jx.la/ ，这个网址在后续爬取的url是相对链接时会按一定规律合并成完整链接。

### 2. 解析搜索接口

1. 浏览器抓搜索接口

   在搜索的时候抓一下请求了什么网址，chrome中直接使用开发者工具即可

<img src="http://zzzia.net:6676/upload/2019/12/parameters-1137218a7f294e77a5fbf8468ed85ad2.png" style="zoom:40%;" />

   我们搜索斗破苍穹，抓到搜索网址，以及他的请求参数：siteid=qula&q=斗破苍穹

   https://sou.xanbhx.com/search?siteid=qula&q=%E6%96%97%E7%A0%B4%E8%8B%8D%E7%A9%B9

   在`searchUrl`中填入网址https://sou.xanbhx.com/search

2. 在`searchMethod`中填入GET/POST方法，这里我们是一个GET方法

3. 搜索接口的参数，填写`searchParam`:q=\{keyword\}，\{keyword\}会被替换成搜索关键词
   1. 如果是GET请求，会拼接成https://sou.xanbhx.com/q=斗破苍穹
     
      这里GET请求还有其他参数，可以修改`searchParam`，例如将`searchParam`改为siteid=qula&q={keyword}，会拼接成https://sou.xanbhx.com/search?siteid=qula&q=%E6%96%97%E7%A0%B4%E8%8B%8D%E7%A9%B9
   2. 如果是POST请求，会post url地址，且以参数形式上传q=斗破苍穹

4. 请注意搜索的文本编码，可在`searchEncode`替换UTF-8/GBK

5. 搜索返回的结果文本编码可以修改`chapterEncodeType`替换UTF-8/GBK

### 3. 解析搜索结果

1. 找到搜索结果集合的Xpath

![搜索结果Xpath](http://zzzia.net:6676/upload/2019/12/image-20191229215342444-dfdc4f95c600458290b44c8ff3381a2d.png)

如图，每一个搜索结果都是一个\<li>，在Chrome中点击元素复制XPath，复制结果第一个li标签的xpath如下

~~~
第一个 li
//*[@id="search-main"]/div[1]/ul/li[1]
第二个 li
//*[@id="search-main"]/div[1]/ul/li[2]
所有 li
//*[@id="search-main"]/div[1]/ul/li
~~~

我们把li标签的父xpath填入`searchBookList`，即//*[@id="search-main"]/div[1]/ul/li

很显然，第一个是我们不想要的，从第二个开始取，使用XPath语法即可搞定

//*[@id="search-main"]/div[1]/ul/li[position()>1]，更多可参考已有json或和我讨论。

2. 解析相关信息，如小说名
  
![搜索结果解析](http://zzzia.net:6676/upload/2019/12/search_bk_name-50c25607641442c6894fed5a73f2fffe.png)
   
   ~~~
   完整xpath
   //*[@id="search-main"]/div[1]/ul/li[2]/span[2]/a
   相对于这一项结果的xpath
   //span[2]/a
   小说名(<a>标签内容)的xpath，xpath语法text()为<a>中的内容
   .//span[2]/a/text()
   小说目录页url的xpath，@href为<a>中的属性
   .//span[2]/a/@href
   ~~~
   
   因此，`searchBookName`=.//span[2]/a/text()，`searchBookUrl`=.//span[2]/a/@href
   
   同理解析出最新章节、分类、作者、更新时间、状态填入即可。
   
   搜索相关的解析书名、url、作者是必须填的。可选项如最新章节、更新时间、分类、状态、封面图片、字数等，可通过完整XPath填入`searchExtraRule`

### 4. 解析章节

1. 找出章节集合的XPath

![catalog_all.png](http://zzzia.net:6676/upload/2019/12/catalog_all-fcd188882f164709873b1b10500f636f.png)

   和搜索结果同样的方法

   ~~~
   第一个章节
   //*[@id="list"]/dl/dd[0]
   第二个章节
   //*[@id="list"]/dl/dd[1]
   所有章节
   //*[@id="list"]/dl/
   ~~~

   `catalogChapterList`=//*[@id="list"]/dl/

   然后以相对xpath解析出书名和章节内容url即可

   `catalogChapterName`=.//dd[1]/a/text()

   `catalogChapterUrl`=.//dd[1]/a/@href

2. 可以追加封面、书籍介绍等元素

   如想要追加一个封面，以绝对xpath设置catalogExtraRule中的imageUrl即可，其他同理

   `imageUrl`=//*[@id="fmimg"]/img

### 5. 解析章节内容

![content.png](http://zzzia.net:6676/upload/2019/12/content-65860c452c5746e5a61b519366b63c2c.png)

直接把文本的xpath填入`chapterLines`即可，可以设置文本编码`chapterEncodeType`

这里是//*[@id="content"]/text()

### 6. 一些其他配置

除了上面必须要配置的以外，还可以配置一些其他的

`cleaner` : 删除广告，一行中如果包含这个字段就删除，用|分隔

`siteClassify` : 用户能看到的站点级别分类，可以按照下载网速、质量决定

`ruleUpdateTime` : 书源更新时间，是一个13位的时间戳，位数不足可补0

`author` : 书源作者


