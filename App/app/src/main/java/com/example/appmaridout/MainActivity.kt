
package com.example.appmaridout



import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText



class MainActivity : AppCompatActivity() {

    private lateinit var textoAmostra: EditText
    private lateinit var mudarTela: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        textoAmostra = findViewById(R.id.textAmostra)
        mudarTela = findViewById(R.id.btn1)

        mudarTela.setOnClickListener(){
            openNextActivity()
        }

    }

    private fun openNextActivity(){
        val intent = Intent(this, DoisActivity::class.java)
        startActivity(intent)
    }
}