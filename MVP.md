## Refactor with MVP arch

- Contract 인터페이스
```kotlin
interface BaseView<T> {
    var presenter: T
}

interface BasePresenter {
    fun start()
}
```

- View, Presenter 인터페이스 (MVP를 적용시킬 Activity 나 Fragment 마다 만들어주면 된다.)<p>
Spec을 보고 Presenter -> View (UI 갱신)은 View에서<br>
View -> Presenter (Business Logic)은 Presenter에서 구현한다.
```kotlin
interface SoundContract {
    interface View : BaseView<Presenter> {
        fun showList(soundList: List<Sound>)
        fun showItem(vararg sound: Sound)
        fun removeItem(vararg sound: Sound)
        fun showAdd()
        fun showRemove()
        fun updateSeekBarMax(max: Int)
        fun updateSeekBarProgress(currentPosition: Int)
        fun updateMediaController(isPlaying: Boolean)
        fun updateCurrentSound(currentSound: Sound?)
    }
    interface Presenter : BasePresenter {
        fun result(requestCode: Int, resultCode: Int)
        fun getAll()
        fun add(vararg sound: Sound)
        fun remove(vararg sound: Sound)
        fun saveFile(context: Context?, filename: String, inputStream: InputStream)
        fun deleteFile(context: Context?, filename: String)
        fun loadFile(context: Context?, filename: String): File?
        fun play(context: Context?, sound: Sound)
        fun stop()
        fun restartOrPause()
        fun seek(millis: Int)
    }
}
```

- implement XXXContract.View (Activity, Fragment 등 XXXContract에 맞는 뷰에 구현한다.)
```kotlin
class SoundMainFragment : Fragment(), SoundContract.View {
    override lateinit var presenter: SoundContract.Presenter //handle business logic like playing music
    ...
    override fun onResume() {
        super.onResume()
        presenter.start()
    }
    ...
    override fun showList(soundList: List<Sound>) {...}
    override fun showItem(vararg sound: Sound) {...}
    override fun removeItem(vararg sound: Sound) {...}
    override fun showAdd() {...}
    override fun showRemove() {...}
    override fun updateSeekBarMax(max: Int) {...}
    override fun updateSeekBarProgress(currentPosition: Int) {...}
    override fun updateMediaController(isPlaying: Boolean) {...}
    override fun updateCurrentSound(currentSound: Sound?) {...}
    ...
}
```

- implement XXXContract.Presenter
```kotlin
class SoundPresenter(private val dao: SoundDao, private val view: SoundContract.View) : SoundContract.Presenter {
    private var mediaPlayer: MediaPlayer? = null
    private var playingThread: Thread? = null
    init {
        view.presenter = this
    }
    override fun start() { ... }
    override fun result(requestCode: Int, resultCode: Int) { ... }
    override fun getAll() { ... }
    override fun add(vararg sound: Sound) { ... }
    override fun remove(vararg sound: Sound) { ... }
    override fun saveFile(context: Context?, filename: String, inputStream: InputStream) { ... }
    override fun deleteFile(context: Context?, filename: String) { ... }
    override fun loadFile(context: Context?, filename: String): File? { ... }
    override fun play(context: Context?, sound: Sound) { ... }
    override fun stop() { ... }
    override fun restartOrPause() { ... }
    override fun seek(millis: Int) { ... }
}
```
