# CoolChat

##### 兼容性：

仅支持在API 21(即Android 5.0)以上的版本

##### 基本功能：

- 聊天：可以发送文字，语音，图片三种类型的消息 
- 广场：就用来发一些动态，只有好友可见

---

##### 软件截图：



| 基本功能                                                     | 广场                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| <img src="http://lc-psmpgrly.cn-n1.lcfile.com/F8oVNDd33QkhcbM7ZtoRilXOIhFUHgVWRyrAUJgq.gif" with="220"> | <img src="http://lc-psmpgrly.cn-n1.lcfile.com/mz4eGGW7LnqEt2xppRmV0n5ERdch5ZcAJOroJD32.gif" with="220"> |
| 登录界面                                                     | 关于界面                                                     |
| <img src="http://lc-psmpgrly.cn-n1.lcfile.com/lVnicf2JydJwNQzxeKM0mRFTMaAwMowK2FJx6qA5.jpg" with="220"> | <img src="http://lc-psmpgrly.cn-n1.lcfile.com/nrSpD55b276M9MMjgLobXHzARZeSw14jExH7J6EV.jpg" with="220"> |

##### 使用到的技术:

- 采用了MVP+MVVM(LiveData+ViewModel)的架构
- 使用RxJava+RxPermissions动态请求权限
- 使用EventBus进行事件的传递 十分方便
- Gson解析数据 一步到位相当好用
- 使用Matisse+Glide+PhotoView来处理一些有关图片的操作
- 通过MediaRecorder来录制语音 MediaPlayer来播放语音
- 使用了一些Material Design的控件 
- 选用Leancloud作为后端支持
- 借助TestIn来检测软件的兼容性（[测试数据](https://github.com/Gennan/CoolChat/blob/master/awesome-res/TestInData.md)）

##### 感想：

:one: 难点

>作为一个即时通讯APP 我认为比较难的一部分 就是如何有效的复用item 通过一个RecyclerView不断的把消息加载出来(我这里是通过隐藏显示的方式来进行)以及有新消息来的时候如何接收通知然后加到里面去 如何能够做到实时刷新消息  还有在加载对话的历史消息的时候的数目(我这里选择了在第一次加载的时候) 还有多个界面如何更好的同步 

:two: 感受

>这次考核有点难受的地方就在Leancloud好像正好在进行版本迭代的时候 用起来有点难受  然后就是有的功能找不到相对应的方法了 就只能自己去魔改 然后就有的地方的交互就没办法很好的实现 UI也就写的有点很简单 不过还是有一点自己当初自己想实现的样子的 还是自己太菜了 东西写的太慢了
