package com.example.appmaridout


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*




class DoisActivity : AppCompatActivity() {

    companion object {
        const val UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB"
        const val DEVICE_NAME = "HC-05"
    }

    private lateinit var switchArd: Switch
    private lateinit var TempButtOn: Button
    private lateinit var TempButtOff: Button
    private lateinit var TempUse: EditText
    private lateinit var TempRealTime: TextView
    private lateinit var MensureBt: Button
    private lateinit var PressReal: TextView
    private lateinit var SistPress: TextView
    private lateinit var DiasPress: TextView
    private lateinit var spinnerSeri: Spinner
    private lateinit var Flownumb: EditText
    private lateinit var BombOn: Button
    private lateinit var BombPause: Button
    private lateinit var BombBack: Button



    private val uiScope = CoroutineScope(Dispatchers.Main + Job())
    private val handlerForTemp = Handler(Looper.getMainLooper())
    private val handlerForPress = Handler(Looper.getMainLooper())
    private val handlerForBombGo = Handler(Looper.getMainLooper())
    private val handlerForBombBack = Handler(Looper.getMainLooper())


    private fun runOnUiThreadSafe(action: () -> Unit) {
        if (!isFinishing) {
            runOnUiThread(action)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dois)

        switchArd = findViewById(R.id.switch1)
        TempButtOn = findViewById(R.id.button2)
        TempButtOff = findViewById(R.id.button)
        spinnerSeri = findViewById(R.id.spinner2)
        TempUse = findViewById(R.id.editTextNumber)
        TempRealTime = findViewById(R.id.textView3)
        MensureBt = findViewById(R.id.button3)
        PressReal = findViewById(R.id.textView10)
        SistPress = findViewById(R.id.textView8)
        DiasPress = findViewById(R.id.textView9)
        Flownumb = findViewById(R.id.editTextNumberDecimal)
        BombOn = findViewById(R.id.button6)
        BombPause = findViewById(R.id.button5)
        BombBack = findViewById(R.id.button4)



        fun createBluetoothConnectionIntent(context: Context, uuid: String, deviceName: String): Intent {
            val intent = Intent(context, BluetoothConnectionActivity::class.java)
            intent.putExtra("UUID", UUID_STRING)
            intent.putExtra("DeviceName", DEVICE_NAME)
            return intent
        }

        //process switch button

        switchArd.setOnCheckedChangeListener { compoundButton, onSwitch ->

            if (onSwitch) {
                Toast.makeText( applicationContext,  getString(R.string.Conectar2),  Toast.LENGTH_SHORT ).show()
                switchArd.text = getString(R.string.Conectar)

                val uuid = "00001101-0000-1000-8000-00805F9B34FB"
                val deviceName = "HC-05"

                val intent = createBluetoothConnectionIntent( applicationContext, uuid, deviceName)
                startActivity(intent)

            }else{
                Toast.makeText( applicationContext,  getString(R.string.Desconect2),  Toast.LENGTH_SHORT ).show()
                switchArd.text = getString(R.string.Desconect)
            }

        }

        TempButtOn.setOnClickListener(){

            if(!tempHandlerRunning){
                startTempHandler()
            }else{
                stopTempHandler()
            }
        }

        TempButtOff.setOnClickListener(){
            stopTempHandler()
            val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket
            val outputStream = bluetoothSocket!!.outputStream
            outputStream.write("l\n".toByteArray())
        }

        var i =0

        MensureBt.setOnClickListener(){
            //Log.d("ButtonClick", "MensureBt clicked")

           /* if(!pressureHandlerRunning){
                startPressHandler()
            }else{
                stopPressHandler()
            }*/

            SistPress.text ="00"
            DiasPress.text ="00"
            val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket
            val outputStream = bluetoothSocket!!.outputStream
            outputStream.write("o\n".toByteArray())
            i = 0
            val handler = Handler(Looper.getMainLooper())


            val updateRunnable = object : Runnable {
                override fun run(){

                    //outputStream.write("o\n".toByteArray())
                    val inputStream = bluetoothSocket!!.inputStream
                    val buffer = ByteArray(32)
                    val bytesRead = inputStream.read(buffer)
                    val VP = String(buffer, 0, bytesRead)

                    //println(VP[0]);

                    if (VP.contains("T")) {
                        val lines = VP.split("\n") // Split VP into individual lines
                        val PressVerdade = lines[1].split("P")[1];
                        PressReal.text = PressVerdade.trim()  // tira espaço do texto
                    }

                    if (VP.contains("S")) {
                        val lines = VP.split("\n") // Split VP into individual lines

                        if (lines.size >= 3) { // Make sure there are at least three lines
                            val secondLine = lines[2].trim() // Get the third line and trim any whitespace
                            SistPress.text = secondLine.split("S")[1]

                        }
                    }

                    if (VP.contains("D")) {
                        println(VP)
                        val lines = VP.split("\n") // Split VP into individual lines

                        if (lines.size >= 3) { // Make sure there are at least three lines
                            val secondLine = lines[2].trim() // Get the third line and trim any whitespace
                            DiasPress.text = secondLine.split("D")[1]

                        }
                        i = 1
                        handler.removeCallbacks(this)
                        PressReal.text = "FIM"

                    }else{
                        handler.postDelayed(this, 500)

                    }
                }
            }

            if (i == 1){
                handler.removeCallbacks(updateRunnable)
                outputStream.write("  \n".toByteArray())
                PressReal.text = "FIM"
            } else{
                handler.postDelayed(updateRunnable, 500)
            }

        }

        spinnerSeri.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                val SpeedS = Flownumb.text.toString()

                Log.d("Spinner", "Selected: $selectedItem")


                if (selectedItem == "XX mL"){
                    println("WAIT")
                }
                if (selectedItem == "20 mL"){
                    val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket
                    val outputStream = bluetoothSocket!!.outputStream
                    outputStream.write("s\n".toByteArray())
                    outputStream.write("20\n".toByteArray())
                    outputStream.write(SpeedS.toByteArray())
                    //println(SpeedS)
                }
                if (selectedItem == "10 mL"){
                    val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket
                    val outputStream = bluetoothSocket!!.outputStream
                    outputStream.write("s\n".toByteArray())
                    outputStream.write("10\n".toByteArray())
                    outputStream.write(SpeedS.toByteArray())
                }
                if (selectedItem == "5 mL"){
                    val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket
                    val outputStream = bluetoothSocket!!.outputStream
                    outputStream.write("s\n".toByteArray())
                    outputStream.write("5\n".toByteArray())
                    outputStream.write(SpeedS.toByteArray())
                }
                if (selectedItem == "3 mL"){
                    val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket
                    val outputStream = bluetoothSocket!!.outputStream
                    outputStream.write("s\n".toByteArray())
                    outputStream.write("3\n".toByteArray())
                    outputStream.write(SpeedS.toByteArray())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // This method is called when nothing is selected.
            }
        }

        BombOn.setOnClickListener(){

            if(!bombHandlerRunning){
                startBombHandler()
            }else{
                stopBombHandler()
            }
        }
        BombPause.setOnClickListener(){

            stopBombHandler()
        }
        BombBack.setOnClickListener(){

            if(!bombHandlerRunning2){
                backBombHandler()
            }else{
                stopBombHandler()
            }
        }

    }

    private var tempHandlerRunning = false


    private val tempRunnable = object : Runnable {
        override fun run() {
            GlobalScope.launch(Dispatchers.IO) {
                /*val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket
                val tempForuse = TempUse.text.toString()
                val outputStream = bluetoothSocket!!.outputStream
                outputStream.write("p\n".toByteArray())
                outputStream.write(tempForuse.toByteArray())*/

                updateUIForTempButton()
            }

            handlerForTemp.postDelayed(this, 500) // Send "o" command every 1 minute and 30 seconds 90000
        }
    }

    private fun startTempHandler() {
        tempHandlerRunning = true
        handlerForTemp.post(tempRunnable)
        val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket
        val tempForuse = TempUse.text.toString()
        val outputStream = bluetoothSocket!!.outputStream
        outputStream.write("p\n".toByteArray())
        outputStream.write(tempForuse.toByteArray())

    }

    private fun stopTempHandler() {
        tempHandlerRunning = false
        handlerForTemp.removeCallbacks(tempRunnable)
    }

    private fun updateUIForTempButton() {
        // Update UI elements after BombOn operation
        runOnUiThreadSafe {
            val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket
            val inputStream = bluetoothSocket!!.inputStream

            val buffer = ByteArray(32)
            val bytesRead = inputStream.read(buffer)
            val TempR = String(buffer, 0, bytesRead)

            if (TempR.contains("T")) {
                val lines = TempR.split("\n") // Split VP into individual lines
                val RealTemp = lines[0].split("T")[1];

                TempRealTime.text = RealTemp.trim()
            }

        }
    }

    /*private var pressureHandlerRunning = false

    private val PressRunnable = object : Runnable{
        override fun run(){
            GlobalScope.launch(Dispatchers.IO){
                //O QUE VAI ESTAR NO LOOP
                val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket
                val outputStream = bluetoothSocket!!.outputStream
                outputStream.write("o\n".toByteArray())

                updateUIForPressButton()
            }

            handlerForPress.postDelayed(this,1000)
        }
    }

    private fun startPressHandler(){
        pressureHandlerRunning = true
        handlerForPress.post(PressRunnable)
    }

    private fun stopPressHandler(){
        pressureHandlerRunning = false
        handlerForPress.removeCallbacks(PressRunnable)
    }
    private fun updateUIForPressButton(){
        runOnUiThreadSafe{
            val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket
            val outputStream = bluetoothSocket!!.outputStream
            val inputStream = bluetoothSocket!!.inputStream
            val buffer = ByteArray(32)
            val bytesRead = inputStream.read(buffer)
            val pressaoR = String(buffer, 0, bytesRead)

            PressReal.text = pressaoR.trim()  // tira espaço do texto

            if (PressReal.text == "Stop"){

                PressReal.text = "00.01"
                outputStream.write("g\n".toByteArray())
                outputStream.write(" \n".toByteArray())
                outputStream.write("g\n".toByteArray())

                stopPressHandler()

            }else{
                startPressHandler()
            }
        }
    }*/

    private var bombHandlerRunning = false
    private var bombHandlerRunning2 = false

    private val bombRunnable = object : Runnable{
        override fun run(){
            GlobalScope.launch(Dispatchers.IO){
                //Código de fazer o motor mover
                val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket

                val outputStream = bluetoothSocket!!.outputStream
                outputStream.write("d\n".toByteArray())

                //updateUIForBombButton()
            }
            handlerForBombGo.postDelayed(this,0)
        }
    }

    private val bombRunnable2 = object : Runnable{
        override fun run(){
            GlobalScope.launch(Dispatchers.IO){
                //Código de fazer o motor mover
                val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket

                val outputStream = bluetoothSocket!!.outputStream
                outputStream.write("e\n".toByteArray())
                //updateUIForBombButton2()
            }
            handlerForBombBack.postDelayed(this,0)
        }
    }

    private fun startBombHandler(){
        //bombHandlerRunning = true
        //handlerForBombGo.post(bombRunnable)
        val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket

        val outputStream = bluetoothSocket!!.outputStream
        outputStream.write("d\n".toByteArray())
    }

    private fun backBombHandler(){
       // bombHandlerRunning2 = true
       // handlerForBombBack.post(bombRunnable2)
        val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket

        val outputStream = bluetoothSocket!!.outputStream
        outputStream.write("e\n".toByteArray())

    }

    private fun stopBombHandler(){
        bombHandlerRunning = false
        handlerForBombGo.removeCallbacks(bombRunnable)
        handlerForBombBack.removeCallbacks(bombRunnable2)
        val bluetoothSocket = BluetoothConnectionActivity.BluetoothConnectionHelper.bluetoothSocket

        val outputStream = bluetoothSocket!!.outputStream
        outputStream.write("z\n".toByteArray())
    }
    /*private fun updateUIForBombButton(){
        runOnUiThreadSafe {

        }
    }
    private fun updateUIForBombButton2(){
        runOnUiThreadSafe {

        }
    }*/
}