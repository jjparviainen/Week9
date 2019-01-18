package com.example.ett15084.moviefinder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static java.lang.Math.toIntExact;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    ScrollView scrollView;
    TextView displayDate;
    int pick;
    String ID;
    NodeList nList;
    TextView displayMovie;
    int thisYear;
    int thisMonth;
    int thisDayOfMonth;
    TextView startEarliest;
    TextView startLatest;
    String timeEarly = "";
    String timeLate= "";
    int hourEarly;
    int minuteEarly;
    int hourLate;
    int minuteLate;
    EditText searchMovie;
    CharSequence searchSequence;
    TextWatcher textWatcher;
    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener timeSetListenerEarly;
    TimePickerDialog.OnTimeSetListener timeSetListenerLate;
    allTheaters aT = allTheaters.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        displayMovie = findViewById(R.id.viewShows);
        searchMovie = findViewById(R.id.searchMovie);



        setSpinner();
        selectDate();
        scrollView();
        setStartEarliest();
        setStartLatest();
        //searchMovie();

    }

    public void setSpinner() {
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<Node> adp = new ArrayAdapter<Node>(this, android.R.layout.simple_spinner_item, aT.readXML());
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adp);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                pick = toIntExact(id);
                ID = aT.theatreList.get(pick).getID();
                System.out.println("Pick main aktivitissa: " + pick);
                //dispList();

            } // to close the onItemSelected

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView textView = findViewById(R.id.textView);
                textView.setText("Dispenser empty");
            }
        });
    }

    public void scrollView() {
        scrollView = findViewById(R.id.scrollView2);

    }

    public void selectDate() {
        displayDate = findViewById(R.id.textView);

        displayDate.setOnClickListener(new View.OnClickListener() { //Tämän avulla saadaan sen hetkinen päivä näkyviin heti kun kalenteri klikataan auki
            @Override
            public void onClick(View v) {
                Calendar cal = new GregorianCalendar();//Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth, dateSetListener, year, month, day);
                dialog.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                thisYear = year;
                thisMonth = month + 1; //tammikuu on kuukausi nro 0
                thisDayOfMonth = dayOfMonth;
                displayMovie.setText(""); // tyhjentää tekstiboksin aiemmistä hakutuloksista
                String date = dayOfMonth + "." + month + "." + year;
                displayDate.setText(date);
                System.out.println("###################           " + thisYear + "       ###      " + thisMonth + "     ###     " + thisDayOfMonth);

                readTheaterXML();

            }
        };

    }

    public void readTheaterXML(/*int y, int m, int d*/) { // Lukee teatterikohtaiset tiedot
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            String urlString = "http://www.finnkino.fi/xml/Schedule/?area=" + ID + "&dt=" + thisDayOfMonth + "." + thisMonth + "." + thisYear;
            Document doc = builder.parse(urlString);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

            nList = doc.getDocumentElement().getElementsByTagName("Show");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String title = element.getElementsByTagName("Title").item(0).getTextContent();
                    String start1 = element.getElementsByTagName("dttmShowStart").item(0).getTextContent().substring(element.getElementsByTagName("dttmShowStart").item(0).getTextContent().lastIndexOf('T') + 1);
                    String start = start1.substring(0, 5);

                    System.out.print("Leffan nimi: ");
                    System.out.println(title);
                    System.out.print("Kellonaika: ");
                    //System.out.println(start.substring(start.lastIndexOf('T') + 1));
                    System.out.println(start);
                    //displayMovie.append(start + "   " + title + "\n");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");

                    if (timeEarly.isEmpty() && timeLate.isEmpty() /*&& title.contains(searchSequence)*/){ //jos molemmat tyhjiä lisätään kaikki päivän elokuvat
                        displayMovie.append(start + "   " + title + "\n");
                    }

                    else if(timeEarly.isEmpty()){ // jos timeEarly on tyhjä niin lisättä laten perusteella
                        if(LocalTime.parse(start).isBefore(LocalTime.parse(timeLate)) || (start.equals(timeLate))) {
                            displayMovie.append(start + "   " + title + "\n");
                        }

                    }

                    else if(timeLate.isEmpty()){ // jos timeLate on tyhjä niin earlyn perusteella
                        if(LocalTime.parse(start).isAfter(LocalTime.parse(timeEarly))  || (start.equals(timeEarly))) {
                            displayMovie.append(start + "   " + title + "\n");
                        }

                    }

                   // else if ((LocalTime.parse(start).isBefore(LocalTime.parse(timeLate)) || (LocalTime.parse(start) == LocalTime.parse(timeLate))) && (LocalTime.parse(start).isAfter(LocalTime.parse(timeEarly)) || (LocalTime.parse(start) == LocalTime.parse(timeEarly)))){
                    else if ((LocalTime.parse(start).isBefore(LocalTime.parse(timeLate)) || (start.equals(timeLate))) && (LocalTime.parse(start).isAfter(LocalTime.parse(timeEarly)) || start.equals(timeEarly))){
                        displayMovie.append(start + "   " + title + "\n");
                    }

                    else{
                        System.out.println("Start time:" + start + " Early time: " + timeEarly + " Late time: " + timeLate);
                    }

                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } finally {
            System.out.println("##########DONE##########");
        }

    }


    public void setStartEarliest() {

        startEarliest = findViewById(R.id.startEarliest);
        startEarliest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = new GregorianCalendar();//Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog TPD = new TimePickerDialog(MainActivity.this, timeSetListenerEarly, hour, minute,DateFormat.is24HourFormat(MainActivity.this));
                TPD.show();
            }
        });

        timeSetListenerEarly = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourEarly = hourOfDay;
                minuteEarly = minute;
                startEarliest.setText(""); // tyhjentää tekstiboksin aiemmasta paskasta
                timeEarly = String.format("%02d:%02d", hourOfDay, minute);
                startEarliest.setText(timeEarly);
                displayMovie.setText(""); // tyhjentää tekstiboksin aiemmistä hakutuloksista
                readTheaterXML();

            }
        };
    }


    public void setStartLatest () {
        startLatest = findViewById(R.id.startLatest);
        startLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = new GregorianCalendar();//Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);
                TimePickerDialog TPD = new TimePickerDialog(MainActivity.this, timeSetListenerLate, hour, minute,DateFormat.is24HourFormat(MainActivity.this));
                TPD.show();
            }
        });

        timeSetListenerLate = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourLate = hourOfDay;
                minuteLate = minute;
                startLatest.setText(""); // tyhjentää tekstiboksin aiemmasta paskasta
                timeLate = String.format("%02d:%02d", hourOfDay, minute);
                startLatest.setText(timeLate);
                displayMovie.setText(""); // tyhjentää tekstiboksin aiemmistä hakutuloksista
                readTheaterXML();
            }
        };
    }

    public void searchMovie(){
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchSequence = s;
                searchMovie.setText(s);
                System.out.println(s);
                readTheaterXML();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        //searchMovie.addTextChangedListener(textWatcher);

    }

}










































