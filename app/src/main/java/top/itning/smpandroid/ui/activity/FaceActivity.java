package top.itning.smpandroid.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import top.itning.smpandroid.R;
import top.itning.smpandroid.R2;
import top.itning.smpandroid.util.CameraUtils;

/**
 * 人脸识别
 *
 * @author itning
 */
@SuppressWarnings("deprecation")
public class FaceActivity extends AppCompatActivity {
    private static final String TAG = "FaceActivity";
    /**
     * 人脸识别 检测视频流中的对象
     */
    private static final CascadeClassifier CASCADE_CLASSIFIER = new CascadeClassifier();
    /**
     * 摄像头预览回调
     */
    private final Camera.PreviewCallback previewCallback;
    /**
     * 摄像头实例
     */
    private Camera camera;
    @BindView(R2.id.sv)
    SurfaceView surfaceView;

    public FaceActivity() {
        previewCallback = new PreviewCallbackImpl(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
    }

    /**
     * 初始化资源和视图
     */
    private void init() {
        checkHasFrontFaceCamera();
        initHaarcascadesData();
        initView();
    }

    /**
     * 检查是否有前置摄像头
     */
    private void checkHasFrontFaceCamera() {
        if (!CameraUtils.hasFrontFaceCamera(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("前置摄像头没有找到")
                    .setMessage("请更换带有前置摄像头的设备再试！")
                    .setCancelable(false)
                    .setNegativeButton("确定", (dialog, which) -> FaceActivity.this.finish())
                    .show();
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    int frontFaceCameraId = CameraUtils.getFrontFaceCameraId();
                    camera = Camera.open(frontFaceCameraId);
                    CameraUtils.setCameraDisplayOrientation(FaceActivity.this, frontFaceCameraId, camera);
                    camera.setPreviewDisplay(holder);
                    camera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "摄像头初始化失败", e);
                    Toast.makeText(FaceActivity.this, "摄像头初始化失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "Surface Destroyed");
                camera.stopPreview();
                holder.removeCallback(this);
                camera.release();
            }
        });
    }

    /**
     * 初始化训练好的数据
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void initHaarcascadesData() {
        File targetFile = new File(getFilesDir(), "haarcascade_frontalface_alt.xml");
        if (!targetFile.exists()) {
            try (InputStream inputStream = this.getAssets().open("haarcascade_frontalface_alt.xml");
                 OutputStream outStream = new FileOutputStream(targetFile)) {
                byte[] buffer = new byte[inputStream.available()];
                inputStream.read(buffer);
                outStream.write(buffer);
                outStream.flush();
                Toast.makeText(this, "初始化模型数据成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "初始化模型数据异常", e);
                Toast.makeText(this, "初始化模型数据异常", Toast.LENGTH_LONG).show();
            }
        }
        // 加载.xml分类器文件
        CASCADE_CLASSIFIER.load(targetFile.getPath());
        Toast.makeText(this, "加载模型数据成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 处理打卡按钮打卡事件
     *
     * @param view View
     */
    public void handleCheckBtnClick(View view) {
        camera.autoFocus((success, camera) -> {
            if (success) {
                camera.setOneShotPreviewCallback(previewCallback);
                camera.cancelAutoFocus();
                Toast.makeText(this, "请保持不动，正在处理", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "对焦失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 预览回调实现
     */
    static class PreviewCallbackImpl implements Camera.PreviewCallback {
        private ObservableEmitter<byte[]> observableEmitter;
        private Camera camera;

        PreviewCallbackImpl(@NonNull FaceActivity activity) {
            Observable
                    .create((ObservableOnSubscribe<byte[]>) emitter -> observableEmitter = emitter)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.computation())
                    .subscribe(new Observer<byte[]>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(byte[] bytes) {
                            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                                YuvImage yuvimage = new YuvImage(bytes, ImageFormat.NV21, previewSize.width, previewSize.height, null);
                                yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);
                                byte[] rawImage = baos.toByteArray();
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                                Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
                                // 转换 Bitmap->Mat
                                AndroidFrameConverter converterToBitmap = new AndroidFrameConverter();
                                OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
                                Frame frame = converterToBitmap.convert(bitmap);
                                Mat mat = converterToMat.convert(frame);
                                opencv_imgproc.cvtColor(mat, mat, opencv_imgproc.CV_RGB2BGR);
                                opencv_core.transpose(mat, mat);
                                opencv_core.flip(mat, mat, -1);

                                RectVector rectVector = new RectVector();
                                CASCADE_CLASSIFIER.detectMultiScale(mat, rectVector);

                                boolean saved = false;
                                String pathName = activity.getExternalCacheDir() + "/" + System.currentTimeMillis() + ".jpg";
                                if (rectVector.size() == 1) {
                                    opencv_imgcodecs.imwrite(new File(pathName).getPath(), mat);
                                    saved = true;
                                }
                                if (saved) {
                                    activity.finishTask(pathName);

                                } else {
                                    activity.makeToast("没有检测到人脸，请再试一次");
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "", e);
                                activity.makeToast("转换识别出现异常");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "", e);
                            activity.makeToast("转换识别出现异常");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            this.camera = camera;
            observableEmitter.onNext(data);
        }
    }

    @Override
    public void onBackPressed() {
        this.setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    /**
     * 土司
     *
     * @param msg 消息
     */
    private void makeToast(@NonNull String msg) {
        runOnUiThread(() -> Toast.makeText(FaceActivity.this, msg, Toast.LENGTH_LONG).show());
    }

    /**
     * 任务完成
     *
     * @param pathName 文件路径
     */
    private void finishTask(@NonNull String pathName) {
        runOnUiThread(() -> {
            Toast.makeText(FaceActivity.this, "识别成功", Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.putExtra("pathName", pathName);
            FaceActivity.this.setResult(RESULT_OK, intent);
            FaceActivity.this.finish();
        });
    }
}
