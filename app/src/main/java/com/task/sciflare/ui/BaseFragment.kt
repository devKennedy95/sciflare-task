package com.task.sciflare.ui

import android.Manifest
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import com.task.sciflare.R
import com.task.sciflare.extensions.hasSMSREADPermission
import com.task.sciflare.extensions.hasSMSSENDPermission
import com.task.sciflare.extensions.smsPermissionArray
import com.task.sciflare.ui.fragment.SmsPermissionRationaleDialogFragment
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    private var _binding: T? = null

    fun getBinding() = _binding!!

    fun hasBinging() = _binding != null

    companion object {
        const val RESULT_OK = "result_ok"
        const val RESULT_CANCELLED = "result_cancelled"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = onCreateViewBinding(inflater, container)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewReady(_binding!!, savedInstanceState)
    }

    abstract fun onCreateViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

    abstract fun onViewReady(binding: T, savedInstanceState: Bundle?)

    protected open fun isSafe(): Boolean =
        !isRemoving && activity != null && !isDetached && isAdded && view != null

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun requestSMSPermission(
        fragment: Fragment,
        requestPermission: ActivityResultLauncher<Array<String>>,
        function: (Boolean) -> Unit,
    ) {
        val permission = smsPermissionArray()
        when {
            requireContext().hasSMSSENDPermission() || requireContext().hasSMSREADPermission() -> {
                function.invoke(true)
            }

            fragment.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                fragment.setFragmentResultListener(RESULT_OK) { _, _ ->
                    fragment.clearFragmentResultListener(RESULT_OK)
                    requestPermission.launch(permission)
                }

                fragment.setFragmentResultListener(RESULT_CANCELLED) { _, _ ->
                    fragment.clearFragmentResultListener(RESULT_CANCELLED)
                    function.invoke(false)
                }

                SmsPermissionRationaleDialogFragment()
                    .show(fragment.parentFragmentManager, ">> Permission")
            }

            else -> requestPermission.launch(permission)
        }
    }

    fun encrypt(strToEncrypt: String): String? {
        val plainText = strToEncrypt.toByteArray(Charsets.UTF_8)
        val initVector = getString(R.string.init_key)
        val key = generateKey(getString(R.string.secret_key))
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(initVector.toByteArray().copyOf(16)))
        val cipherText = cipher.doFinal(plainText)
        return Base64.encodeToString(cipherText, Base64.DEFAULT);
    }

    fun decrypt(
        dataToDecrypt: String?
    ): String {
        val initVector = getString(R.string.init_key)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        val key = generateKey(getString(R.string.secret_key))
        cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(initVector.toByteArray().copyOf(16)))
        val cipherText = cipher.doFinal(Base64.decode(dataToDecrypt, Base64.DEFAULT))
        return buildString(cipherText)
    }

    private fun generateKey(password: String): SecretKeySpec {
        val digest: MessageDigest = MessageDigest.getInstance("SHA-256")
        val bytes = password.toByteArray()
        digest.update(bytes, 0, bytes.size)
        val key = digest.digest()
        return SecretKeySpec(key, "AES")
    }

    private fun buildString(text: ByteArray): String {
        val sb = StringBuilder()
        for (char in text) {
            sb.append(char.toInt().toChar())
        }
        return sb.toString()
    }
}