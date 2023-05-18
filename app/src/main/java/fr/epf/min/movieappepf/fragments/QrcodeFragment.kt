package fr.epf.min.movieappepf.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.*
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import fr.epf.min.movieappepf.R
import java.util.concurrent.Semaphore

class QrcodeFragment : Fragment(), SurfaceHolder.Callback {
    private lateinit var fragmentContext: Context
    private lateinit var surfaceHolder: SurfaceHolder
    private val cameraManager: CameraManager by lazy {
        fragmentContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureSession: CameraCaptureSession
    private lateinit var backgroundThread: HandlerThread
    private lateinit var backgroundHandler: Handler
    private val cameraOpenCloseLock = Semaphore(1)

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 200
        private const val TAG = "QRScanner"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_qrcode, container, false)
        val surfaceView = view.findViewById(R.id.cameraPreview) as SurfaceView
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)
        return view
    }




    override fun onDestroyView() {
        super.onDestroyView()
        surfaceHolder.removeCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        startBackgroundThread()
        requestCameraPermission()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // Handle surface changes if needed
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        closeCamera()
        stopBackgroundThread()
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
        backgroundHandler = Handler(backgroundThread.looper)
    }

    private fun stopBackgroundThread() {
        backgroundThread.quitSafely()
        try {
            backgroundThread.join()
        } catch (e: InterruptedException) {
            Log.e(TAG, "Interrupted while stopping background thread", e)
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            openCamera()
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraOpenCloseLock.release()
            cameraDevice = camera
            createCameraPreviewSession()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraOpenCloseLock.release()
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            onDisconnected(camera)
            Toast.makeText(context, "Camera error: $error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        try {
            val cameraId = cameraManager.cameraIdList[0]
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                cameraManager.openCamera(cameraId, stateCallback, backgroundHandler)
            }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Cannot open camera", e)
            Toast.makeText(context, "Cannot open camera.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createCameraPreviewSession() {
        try {
            val surface = surfaceHolder.surface
            val captureRequestBuilder =
                cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder.addTarget(surface)

            cameraDevice.createCaptureSession(
                listOf(surface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        captureSession = session
                        captureRequestBuilder.set(
                            CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                        )
                        session.setRepeatingRequest(
                            captureRequestBuilder.build(),
                            null,
                            backgroundHandler
                        )
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        Log.e(TAG, "Failed to configure camera preview.")
                    }
                },
                backgroundHandler
            )
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Cannot create camera capture session", e)
        }
    }

    private fun closeCamera() {
        try {
            cameraOpenCloseLock.acquire()
            captureSession.close()
            cameraDevice.close()
        } catch (e: InterruptedException) {
            throw RuntimeException("Interrupted while trying to lock camera closing.", e)
        } finally {
            cameraOpenCloseLock.release()
        }
    }
}
