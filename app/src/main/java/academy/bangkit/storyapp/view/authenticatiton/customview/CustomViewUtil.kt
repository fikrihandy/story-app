package academy.bangkit.storyapp.view.authenticatiton.customview

import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout

object CustomViewUtil {
    fun setupCustomView(
        email: MyEditText,
        password: MyEditText,
        button: Button,
        passwordLayout: TextInputLayout,
        name: EditText? = null,
        loginPage: Boolean? = null,
    ) {

        if (loginPage == false) {
            name?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val s1 = s.toString()
                    if (s1.isEmpty()) {
                        name.error = "Name is required"
                    } else {
                        name.error = null
                    }
                    setRegisterButtonEnable(
                        name.text.toString(),
                        email.text.toString(),
                        password.text.toString(),
                        button
                    )
                }

                override fun afterTextChanged(s: Editable?) {}

            })
        }


        fun setButton() {
            if (loginPage == false) {
                setRegisterButtonEnable(
                    name?.text.toString(),
                    email.text.toString(),
                    password.text.toString(),
                    button
                )
            } else {
                setLoginButtonEnable(email.text.toString(), password.text.toString(), button)
            }
        }

        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val s2 = s.toString()
                if (s2.isEmpty()) {
                    email.error = "Email is required"
                } else {
                    email.error = null
                }
                setButton()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val s3 = s.toString()
                if (s3.length < 8) {
                    password.error = "Min 8 char!!"
                    passwordLayout.endIconMode = TextInputLayout.END_ICON_NONE
                } else {
                    password.error = null
                    passwordLayout.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                }
                setButton()
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun setLoginButtonEnable(
        email: String,
        password: String,
        button: Button,
    ) {
        button.isEnabled = email.isNotEmpty() && password.length >= 8
    }

    fun setRegisterButtonEnable(
        name: String,
        email: String,
        password: String,
        button: Button,
    ) {
        button.isEnabled =
            name.isNotEmpty() && email.isNotEmpty() && password.length >= 8
    }
}