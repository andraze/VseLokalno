package diplomska.naloga.vselokalno.ImageCrop

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.options

class ImageCropper (activity : FragmentActivity, context: Context) {

    lateinit var mCallback: ImageCroppedCallbackListener

    interface ImageCroppedCallbackListener {
        fun onImageCroppedCallback(path: String)
    }

    private val cropImage = activity.registerForActivityResult(CropImageContract()) { result ->
        Log.d("ImageCropper", "Got here")
        if (result.isSuccessful) {
            // Use the returned uri
//            val uriContent = result.originalUri //result.uriContent
            val uriFilePath = result.getUriFilePath(context)
            if (uriFilePath != null) {
                mCallback.onImageCroppedCallback(uriFilePath.toString())
            } else {
                Log.w("ImageCropper", "uriContent = null!")
            }
            Log.d("ImageCropper", uriFilePath + "\n" + uriFilePath)
        } else {
            // An error occurred
            Log.w("ImageCropper", result.error)
        }
    }

    fun startCrop(mUri: Uri, callbackTemp: ImageCroppedCallbackListener) {
        mCallback = callbackTemp
        cropImage.launch(
            options(uri = mUri) {
                setAspectRatio(400, 300)
                setActivityTitle("Obreži sliko")
                setOutputCompressFormat(Bitmap.CompressFormat.PNG)
                setRequestedSize(600, 450)
            }
        )
    }
}