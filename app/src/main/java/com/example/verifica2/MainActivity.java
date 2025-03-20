package com.example.verifica2;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText budgetEditText;
    private Button impostaBudgetButton;
    private Button ciboButton;
    private Button trasportiButton;
    private Button altroButton;
    private Button ricavoButton;
    private EditText descrizioneEditText;
    private EditText importoEditText;
    private Button inviaButton;
    private TextView resocontoTextView;

    private double budgetIniziale = 0;
    private double budgetAttuale = 0;
    private Map<String, Double> spesePerCategoria = new HashMap<>();
    private double totaleSpese = 0;
    private double totaleRicavi = 0;
    private boolean budgetImpostato = false;
    private Button lastClickedCategoryButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        budgetEditText = findViewById(R.id.Budget);
        impostaBudgetButton = findViewById(R.id.impostabutton);
        ciboButton = findViewById(R.id.buttonCibo);
        trasportiButton = findViewById(R.id.buttonTrasporti);
        altroButton = findViewById(R.id.buttonAltro);
        ricavoButton = findViewById(R.id.buttonRicavo);
        descrizioneEditText = findViewById(R.id.editTextDescrizione);
        importoEditText = findViewById(R.id.editTextImporto);
        inviaButton = findViewById(R.id.buttonInvia);
        resocontoTextView = findViewById(R.id.resoconto);

        // Inizialmente disabilita i bottoni di categoria e il bottone invia
        ciboButton.setEnabled(false);
        trasportiButton.setEnabled(false);
        altroButton.setEnabled(false);
        ricavoButton.setEnabled(false);
        inviaButton.setEnabled(false);

        // Nascondi i campi Descrizione e Importo
        descrizioneEditText.setVisibility(View.GONE);
        importoEditText.setVisibility(View.GONE);
        resocontoTextView.setVisibility(View.GONE);

        // Listener per il bottone Imposta Budget
        impostaBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    budgetIniziale = Double.parseDouble(budgetEditText.getText().toString());
                    if (budgetIniziale > 0) {
                        budgetAttuale = budgetIniziale;
                        budgetEditText.setEnabled(false);
                        impostaBudgetButton.setEnabled(false);
                        ciboButton.setEnabled(true);
                        trasportiButton.setEnabled(true);
                        altroButton.setEnabled(true);
                        ricavoButton.setEnabled(true);
                        budgetImpostato = true;
                        resocontoTextView.setVisibility(View.VISIBLE); // Make it visible here!
                    } else {
                        Toast.makeText(MainActivity.this, "Il budget deve essere un numero positivo", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Inserisci un budget valido", Toast.LENGTH_SHORT).show();
                }
            }
        });


        View.OnClickListener categoriaClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descrizioneEditText.setVisibility(View.VISIBLE);
                importoEditText.setVisibility(View.VISIBLE);
                inviaButton.setEnabled(true);
                lastClickedCategoryButton = (Button) v;

                // Rimuovi il testo di esempio quando l'utente inizia a scrivere
                descrizioneEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            descrizioneEditText.setHint("");
                        } else {
                            descrizioneEditText.setHint("Descrizione");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                importoEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() > 0) {
                            importoEditText.setHint("");
                        } else {
                            importoEditText.setHint("Importo($)");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        };

        ciboButton.setOnClickListener(categoriaClickListener);
        trasportiButton.setOnClickListener(categoriaClickListener);
        altroButton.setOnClickListener(categoriaClickListener);
        ricavoButton.setOnClickListener(categoriaClickListener);

        // Listener per il bottone Invia
        inviaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descrizione = descrizioneEditText.getText().toString();
                try {
                    double importo = Double.parseDouble(importoEditText.getText().toString());
                    String categoria = "";
                    if (lastClickedCategoryButton != null) {
                        categoria = lastClickedCategoryButton.getText().toString();
                    }
                    // Check if the category is not "Ricavo" before applying the limit
                    if (!categoria.equals("Ricavo") && importo > 250) {
                        Toast.makeText(MainActivity.this, "L'importo non può superare 250 euro", Toast.LENGTH_SHORT).show();
                        disableCategoriaButtons();
                        return;
                    }

                    if (!categoria.isEmpty()) {
                        if (categoria.equals("Ricavo")) {
                            budgetAttuale += importo;
                            totaleRicavi += importo;
                            if (spesePerCategoria.containsKey(categoria)) {
                                spesePerCategoria.put(categoria, spesePerCategoria.get(categoria) + importo);
                            } else {
                                spesePerCategoria.put(categoria, importo);
                            }
                        } else {
                            budgetAttuale -= importo;
                            totaleSpese += importo;
                            if (spesePerCategoria.containsKey(categoria)) {
                                spesePerCategoria.put(categoria, spesePerCategoria.get(categoria) + importo);
                            } else {
                                spesePerCategoria.put(categoria, importo);
                            }
                        }
                        aggiornaResoconto();
                        descrizioneEditText.setText("");
                        importoEditText.setText("");
                        descrizioneEditText.setHint("Descrizione");
                        importoEditText.setHint("Importo($)");
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Inserisci un importo valido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void aggiornaResoconto() {
        StringBuilder resoconto = new StringBuilder();
        resoconto.append("Budget Iniziale: ").append(String.format(Locale.getDefault(), "%.2f", budgetIniziale)).append("€\n");
        resoconto.append("Totale spese e ricavi: ").append(String.format(Locale.getDefault(), "%.2f", totaleRicavi - totaleSpese)).append("€\n");
        resoconto.append("Cibo: ").append(String.format(Locale.getDefault(), "%.2f", spesePerCategoria.getOrDefault("Cibo", 0.0))).append("€");
        if (totaleSpese > 0 && spesePerCategoria.containsKey("Cibo")) {
            double percentuale = (spesePerCategoria.get("Cibo") / totaleSpese) * 100;
            resoconto.append(" (").append(String.format(Locale.getDefault(), "%.2f", percentuale)).append("%)");
        }
        resoconto.append("\n");
        resoconto.append("Trasporti: ").append(String.format(Locale.getDefault(), "%.2f", spesePerCategoria.getOrDefault("Trasporti", 0.0))).append("€");
        if (totaleSpese > 0 && spesePerCategoria.containsKey("Trasporti")) {
            double percentuale = (spesePerCategoria.get("Trasporti") / totaleSpese) * 100;
            resoconto.append(" (").append(String.format(Locale.getDefault(), "%.2f", percentuale)).append("%)");
        }
        resoconto.append("\n");
        resoconto.append("Altro: ").append(String.format(Locale.getDefault(), "%.2f", spesePerCategoria.getOrDefault("Altro", 0.0))).append("€");
        if (totaleSpese > 0 && spesePerCategoria.containsKey("Altro")) {
            double percentuale = (spesePerCategoria.get("Altro") / totaleSpese) * 100;
            resoconto.append(" (").append(String.format(Locale.getDefault(), "%.2f", percentuale)).append("%)");
        }
        resoconto.append("\n");
        resoconto.append("Ricavo: ").append(String.format(Locale.getDefault(), "%.2f", spesePerCategoria.getOrDefault("Ricavo", 0.0))).append("€\n");
        resoconto.append("Totale generale: ").append(String.format(Locale.getDefault(), "%.2f", budgetAttuale)).append("€");
        resocontoTextView.setText(resoconto.toString());
    }

    private void disableCategoriaButtons() {
        ciboButton.setEnabled(false);
        trasportiButton.setEnabled(false);
        altroButton.setEnabled(false);
        ricavoButton.setEnabled(false);
        inviaButton.setEnabled(false);
    }
}