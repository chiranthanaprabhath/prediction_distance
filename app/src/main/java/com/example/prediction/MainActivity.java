package com.example.prediction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;

public class MainActivity extends AppCompatActivity {
    TextView resultx;
    FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder()
            .setAssetFilePath("model.tflite")
            .build();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultx=findViewById(R.id.resultx);
        FirebaseModelInterpreter interpreter;
        try {
            FirebaseModelInterpreterOptions options =
                    new FirebaseModelInterpreterOptions.Builder(localModel).build();
            interpreter = FirebaseModelInterpreter.getInstance(options);
            try{
                FirebaseModelInputOutputOptions inputOutputOptions =
                        new FirebaseModelInputOutputOptions.Builder()
                                .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1,8})
                                .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1,1})
                                .build();
                float[][] input = new float[1][8];
                input[0][0]=4500.0f;//sender Berrtty capasity
                input[0][1]=64.0f;//sender battrey level
                input[0][2]=75.0f;//reaciver battery level
                input[0][3]=4.0f;//sender ble version
                input[0][4]=54.0f;//sender tempature
                input[0][5]=46.0f;//reciver temperature
                input[0][6]=4.1f;//reciver blu version
                input[0][7]=-53.0f;// rssi value
                FirebaseModelInputs inputs = new FirebaseModelInputs.Builder()
                        .add(input)  // add() as many input arrays as your model requires
                        .build();
                interpreter.run(inputs, inputOutputOptions)
                        .addOnSuccessListener(
                                new OnSuccessListener<FirebaseModelOutputs>() {
                                    @Override
                                    public void onSuccess(FirebaseModelOutputs result) {
                                        float[][] output = result.getOutput(0);//answers of prdiction
                                        float probabilities = output[0][0];//answers of prdiction
                                        resultx.setText(String.valueOf(probabilities));
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });

            }catch (FirebaseMLException er){
            }
        } catch (FirebaseMLException e) {

        }
    }
}
