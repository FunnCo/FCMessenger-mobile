package com.funnco.fcmessenger.activity.main.ui.settings

import android.R.attr
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.funnco.fcmessenger.activity.login.LoginActivity
import com.funnco.fcmessenger.common.model.UserModel
import com.funnco.fcmessenger.common.retrofit.RetrofitObject
import com.funnco.fcmessenger.databinding.FragmentSettingsBinding
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val READ_CONTENT_CODE = 90002
    private var selectedImage: Uri? = null
    private lateinit var remoteImageLink: String
    private var isFirstLaunch = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        showCurrentInfo()
        binding.settingsBtnChangeSave.setOnClickListener {
            uploadNewUserInfo()
        }

        binding.settingsBtnChangeAvatar.setOnClickListener {
            updateImage()
        }

        binding.settingsBtnLeaveAll.setOnClickListener {
            val prefs = activity!!.getSharedPreferences("settings", MODE_PRIVATE)
            prefs.edit().putString("token", "").apply()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return binding.root
    }

    private fun showCurrentAvatar() {
        Picasso.get()
            .load("http://45.90.216.162/resources/image/user/$remoteImageLink")
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .into(binding.settingsImgAvatar)
    }

    private fun showCurrentInfo() {
        val prefs = activity!!.getSharedPreferences("settings", MODE_PRIVATE)
        val token = prefs.getString("token", null)

        RetrofitObject.userAPI.getUserInfo(token!!, null).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.isSuccessful && response.code() == 200) {
                    val user = response.body()!!

                    binding.settingsEtxtEmail.setText(user.email)
                    binding.settingsEtxtFirstname.setText(user.firstname)
                    binding.settingsEtxtLastname.setText(user.lastname)
                    binding.settingsEtxtPhone.setText(user.phone)
                    binding.settingsEtxtPatronymic.setText(user.patronymic)

                    remoteImageLink = user.avatarFilename!!
                    if(isFirstLaunch) {
                        showCurrentAvatar()
                        isFirstLaunch=false
                    }


                } else {
                    Log.d(this.javaClass.name, "Request is not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                Log.d(this.javaClass.name, "Request is not successful: ${t.message}")
            }

        })
    }

    private fun uploadNewUserInfo() {
        val newUser = UserModel()
        newUser.email = binding.settingsEtxtEmail.text.toString()
        newUser.phone = binding.settingsEtxtPhone.text.toString()
        newUser.firstname = binding.settingsEtxtFirstname.text.toString()
        newUser.lastname = binding.settingsEtxtLastname.text.toString()
        newUser.patronymic = binding.settingsEtxtPatronymic.text.toString()
        newUser.password = binding.settingsEtxtPassword.text.toString()
        val repeatedPassword = binding.settingsEtxtRepeatPassword.text.toString()
        // Проверка что все поля заполнены
        if (newUser.email.isNullOrBlank() ||
            newUser.phone.isNullOrBlank() ||
            newUser.firstname.isNullOrBlank() ||
            newUser.lastname.isNullOrBlank()
        ) {
            showAlert("Пожалуйста, заполните все поля (отчество указывать не обязательно).")
            return
        }

        // Проверка на соотвествие почты и телефона паттерну
        val isPhoneCorrect = newUser.phone!!.matches("^\\+7\\d{10}$".toRegex())
        if (!isPhoneCorrect) {
            showAlert("Пожалуйста, введите корректный номер телефона в следующем формате: +71234567890")
            return
        }

        val isEmailCorrect = newUser.email!!.matches(
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$".toRegex()
        )
        if (!isEmailCorrect) {
            showAlert("Пожалуйста, введите корректный адрес электронной почты")
            return
        }

        // Проверка на совпадение паролей
        if (binding.settingsEtxtPassword.text.isNotBlank() && binding.settingsEtxtRepeatPassword.text.isNotBlank()) {
            if (repeatedPassword != newUser.password) {
                showAlert("Введенные пароли не совпадают")
                return
            }
        } else {
            newUser.password = null
        }

        val prefs = activity!!.getSharedPreferences("settings", MODE_PRIVATE)
        val token = prefs.getString("token", null)

        RetrofitObject.userAPI.changeAbout(token!!, newUser).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful && response.code() == 200) {
                    showCurrentInfo()
                    Toast.makeText(context!!, "Данные успешно обновлены", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(this.javaClass.name, "Request is not successful: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d(this.javaClass.name, "Request is not successful: ${t.message}")
            }

        })

        if (selectedImage != null) {

            val bitmap = binding.settingsImgAvatar.drawable.toBitmap()
            val requsetBody = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                File(getPath(bitmap, "image"))
            )
            val part = MultipartBody.Part.createFormData("image", "", requsetBody)

            RetrofitObject.userAPI.changeAvatar(token, part).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful && response.code() == 200) {

                        Toast.makeText(context!!, "Аватарка успешно обновлена", Toast.LENGTH_SHORT)
                            .show()
                        Log.d(this.javaClass.name, "Request is successful: ${response.code()}")
                    } else {
                        Log.d(this.javaClass.name, "Request is not successful: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d(this.javaClass.name, "Request is not successful: ${t.cause}")
                }

            })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == READ_CONTENT_CODE && data?.data != null) {
            Log.i("ImageSelection", data.data?.toString()!!)
            Picasso.get().load(data.data!!).into(binding.settingsImgAvatar)
            selectedImage = data.data!!
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, READ_CONTENT_CODE)
    }

    private fun showAlert(message: String) {
        AlertDialog.Builder(context!!)
            .setMessage(message)
            .setNeutralButton("OK") { dialogInterface, _ ->
                dialogInterface.cancel()
            }.create().show()
    }

    private fun getPath(bitmap: Bitmap, name: String): String {
        val filesDir: File = context!!.getFilesDir()
        val imageFile = File(filesDir, "$name.jpg")

        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }
        return imageFile.absolutePath

    }

}