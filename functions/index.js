const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

//Member Fuzzy Tujuan (Jumlah Air)
//var air = [1,2.25,3.5,5,6.625,7.75,9];
const air = [0, 0.5, 0.78, 1.11, 1.47, 1.72, 2];

//GLOBAL FUNCTIONS
function tambahNol(val) {
    if(val<10){
        val = '0' + val;
    }
    return val;
}
function ambilHari(val){
    var dd;
    switch(val){
        case 0:
            dd = "Sun";
            break;
        case 1:
            dd = "Mon";
            break;
        case 2:
            dd = "Tue";
            break;
        case 3:
            dd = "Wed";
            break;
        case 4:
            dd = "Thu";
            break;
        case 5:
            dd = "Fri";
            break;
        case 6:
            dd = "Sat";
            break;
    }
    return dd;
}
function ambilBulan(val){
    switch(val){
        case 0:
            mm = "JAN";
            break;
        case 1:
            mm = "FEB";
            break;
        case 2:
            mm = "MAR";
            break;
        case 3:
            mm = "APR";
            break;
        case 4:
            mm = "MAY";
            break;
        case 5:
            mm = "JUN";
            break;
        case 6:
            mm = "JUL";
            break;
        case 7:
            mm = "AUG";
            break;
        case 8:
            mm = "SEP";
            break;
        case 9:
            mm = "OCT";
            break;
        case 10:
            mm = "NOV";
            break;
        case 11:
            mm = "DES";
            break;
    }
    return mm;
}

//Fungsi Hitung segi-3 pada fuzzy
function fSegi3(nilai,p1,p2,p3){    
    if((nilai<=p1)||(nilai>=p3)){
        memberFuzzy = 0;
    }else if((nilai>=p1)&&(nilai<p2)){
        memberFuzzy = (nilai-p1)/(p2-p1);
    }else if((nilai>=p2)&&(nilai<=p3)){
        memberFuzzy = (p3-nilai)/(p3-p2);
    }else {
        memberFuzzy = 0;
    }
}
//Fungsi Hitung segi-4 pada fuzzy
function fSegi4(nilai,p1,p2,p3,p4){
    if((nilai<=p1)||(nilai>p4)){
        memberFuzzy = 0;
    }else if((nilai>=p1)&&(nilai<p2)){
        memberFuzzy = (nilai-p1)/(p2-p1);
    }else if((nilai>=p2)&&(nilai<=p3)){
        memberFuzzy = 1;
    }else if((nilai>=p3)&&(nilai<p4)){
        memberFuzzy = (p4-nilai)/(p4-p3);
    }else{
        memberFuzzy = 0;
    }
}

exports.mkHistory = functions.database.ref('Trigger/aMinute').onUpdate(change => {
    const before    = change.before.val();
    const after     = change.after.val();

    //Supaya tidak sama
    if(before === after){
        console.log('nothing change');
        return null;
    }

    const refSoil       = admin.database().ref('Realtime/rtSoil');
    const refTemp       = admin.database().ref('Realtime/rtTemp');
    const refMinSoil    = admin.database().ref('Riwayat/rwSoil/rwMinute');
    const refMinTemp    = admin.database().ref('Riwayat/rwTemp/rwMinute');
    const refHourSoil   = admin.database().ref('Riwayat/rwSoil/rwHour');
    const refHourTemp   = admin.database().ref('Riwayat/rwTemp/rwHour');
    const refHourWater  = admin.database().ref('Riwayat/rwWater/rwHour');
    
    var sum     = 0;
    var item    = 0;
    var length  = 0;
    var result  = 0;

    //GET TIME
    //var n = new Date();
    var n = new Date(new Date().toLocaleString("en-US", {timeZone: "Asia/Jakarta"}));
    var s = n.getSeconds();
    var m = n.getMinutes();
    var h = n.getHours();
    var d = n.getDate();
    
    //Get Name of the Day and the Month
    var cekMonth = n.getMonth();
    var cekDay = n.getDay();
    var dd = ambilHari(cekDay);
    var mm = ambilBulan(cekMonth);
    //Tambah '0'
    h = tambahNol(h);
    m = tambahNol(m);
    s = tambahNol(s);
    d = tambahNol(d);
    //Set Time
    var minute  = h + ':' + m;
    var hour    = h + ':00';
    var hour1   = h + ':01';
    var hour2   = h + ':02';
    var day     = d + ' ' + mm + ', ' + dd;

    //Sama tidak dengan menit?
    function cekMinutes(val){
        return after.localeCompare(val);
    }

    //Get Week
    // var firstWeekday = new Date(n.getFullYear(), n.getMonth(), 1).getDay();
    // var offsetDate = n.getDate() + firstWeekday - 1;
    // var w = Math.floor(offsetDate / 7)+1;
    // var week    = mm + ' Week ' + w;

    //rwMinute
    refSoil.once('value', snapshot => {
        var val = snapshot.val();
        admin.database().ref('/Riwayat/rwSoil/rwMinute/' + after).set(val);
        admin.database().ref('Fuzzy/fzSource/fzSoil').set(val);
    });
    refTemp.once('value', snapshot => {
        var val = snapshot.val();
        admin.database().ref('/Riwayat/rwTemp/rwMinute/' + after).set(val);
        admin.database().ref('Fuzzy/fzSource/fzTemp').set(val);
    });

    //rwHour
    if(cekMinutes(hour)===0){
        refMinSoil.once('value', snapshot => {
            snapshot.forEach(childSnapshot => {
                item = childSnapshot.val();
                sum = sum + item;
                length = length + 1;
            });
            result  = sum/length;
            result  = Math.round((result + Number.EPSILON) * 100) / 100;
            sum = 0; length = 0;
            admin.database().ref('/Riwayat/rwSoil/rwHour/' + hour).set(result);
        });
        refMinTemp.once('value', snapshot => {
            snapshot.forEach(childSnapshot => {
                item = childSnapshot.val();
                sum = sum + item;
                length = length + 1;
            });
            result  = sum/length;
            result  = Math.round((result + Number.EPSILON) * 100) / 100;
            sum = 0; length = 0;
            admin.database().ref('/Riwayat/rwTemp/rwHour/' + hour).set(result);
        });   
    }

    //rwDay
    if(cekMinutes('00:00')===0){
        refHourSoil.once('value', snapshot => {
            snapshot.forEach(childSnapshot => {
                item = childSnapshot.val();
                sum = sum + item;
                length = length + 1;
            });
            result  = sum/length;
            result  = Math.round((result + Number.EPSILON) * 100) / 100;
            sum = 0; length = 0;
            admin.database().ref('/Riwayat/rwSoil/rwDay/' + day).set(result);   
        });
        refHourTemp.once('value', snapshot => {
            snapshot.forEach(childSnapshot => {
                item = childSnapshot.val();
                sum = sum + item;
                length = length + 1;
            });
            result  = sum/length;
            result  = Math.round((result + Number.EPSILON) * 100) / 100;
            sum = 0; length = 0;
            admin.database().ref('/Riwayat/rwTemp/rwDay/' + day).set(result);   
        });
        refHourWater.once('value', snapshot => {
            snapshot.forEach(childSnapshot => {
                item = childSnapshot.val();
                sum = sum + item;
            });
            result  = sum;
            result  = Math.round((result + Number.EPSILON) * 100) / 100;
            sum = 0; length = 0;
            admin.database().ref('/Riwayat/rwWater/rwDay/' + day).set(result);   
        });  
    }

    //Method Hapus, Supaya  db Tidak Penuh
    if(cekMinutes(hour1)===0){
        refMinSoil.remove();
        refMinTemp.remove(); 
    }
    if(cekMinutes('00:01')===0){
        refHourSoil.remove();
        refHourTemp.remove();
        refHourWater.remove();
    }
    
    return null;
});

exports.setTrigger = functions.database.ref('Trigger/second').onUpdate(change => {
    const before    = change.before.val();
    const after     = change.after.val();

    //Supaya tidak sama
    if(before === after){
        console.log('nothing change');
        return null;
    }
    
    admin.database().ref('Realtime/rtSoil').once('value', snapshot => {
        var soil = snapshot.val();
        var notifSoil;

        if(soil<40)        { notifSoil = "DRY";  }
        else if(soil<67.5) { notifSoil = "MOIST";}
        else               { notifSoil = "WET";  }

        admin.database().ref('Notifikasi/soil').set(notifSoil);       
    });
    
    admin.database().ref('Realtime/rtTemp').once('value', snapshot => {
        var temp = snapshot.val();
        var notifTemp;
        if(temp<22.5)      {notifTemp = "COLD";    }
        else if(temp<27.5) {notifTemp = "COOL";    }
        else if(temp<32.5) {notifTemp = "NORMAL";  }
        else if(temp<37.5) {notifTemp = "WARM";    }
        else               {notifTemp = "HOT";     }
        admin.database().ref('Notifikasi/temp').set(notifTemp);        
    });

    //GET TIME
    var n = new Date(new Date().toLocaleString("en-US", {timeZone: "Asia/Jakarta"}));
    //var n = new Date();
    //var s = n.getSeconds();
    var m = n.getMinutes();
    var h = n.getHours();
    //var d = n.getDate();
    //Get Week
    //var firstWeekday = new Date(n.getFullYear(), n.getMonth(), 1).getDay();
    //var offsetDate = n.getDate() + firstWeekday - 1;
    //var w = Math.floor(offsetDate / 7)+1;
    //Get Name of the Day and the Month
    // var cekMonth = n.getMonth();
    // var cekDay = n.getDay();
    // var dd = ambilHari(cekDay);
    // var mm = ambilBulan(cekMonth);
    //Tambah '0'
    h = tambahNol(h);
    m = tambahNol(m);
    // s = tambahNol(s);
    // d = tambahNol(d);
    //Set Time
    var minute  = h + ':' + m;
    //var hour    = h + ':30';
    // var day     = d + ' ' + dd;
    // var week    = mm + ' Week ' + w;

    
    admin.database().ref('Trigger/aMinute').set(minute);
    admin.database().ref('Relay/time/time1').once('value', snapshot => {
        var value = snapshot.val();
        if(value === minute) { admin.database().ref('Trigger/bHour').set(minute); }
    });
    admin.database().ref('Relay/time/time2').once('value', snapshot => {
        var value = snapshot.val();
        if(value === minute) { admin.database().ref('Trigger/bHour').set(minute); }
    });
    
    if(m===45){ admin.database().ref('Trigger/bHour').set(minute);}
    if(m===30){ admin.database().ref('Trigger/bHour').set(minute);}
    if(m===15){ admin.database().ref('Trigger/bHour').set(minute); }
    
    // admin.database().ref('Trigger/cDay').set(day);
    // admin.database().ref('Trigger/dWeek').set(week);
    
    return null;
});


exports.notifSoil = functions.database.ref('Notifikasi/soil').onUpdate(change => {
    const before = change.before.val();
    const after = change.after.val();

    if(before === after){
        console.log('nothing change');
        return null;
    }

    admin.database().ref('/Realtime/rtSoil').once('value', snapshot => {
        var value = snapshot.val();
        admin.database().ref('Device/Token').once('value', childSnapshot => {
            var app_token = childSnapshot.val();
            var message = {
                notification: {
                    title: after,
                    body: 'Moist Soil Reaches ' + value +  '%'
                },
                 token: app_token
            };
            if(after==='DRY'){
                admin.messaging().send(message).then(response => {
                    console.log('Success: ', response);
                    return response;
                }).catch(error => {
                    console.log('error: ',error);
                    return error;
                });
            }else return console.log('Notif Soil Tidak Jadi');
            return null;
        });
    });
    return null;
});

exports.notifTemp = functions.database.ref('Notifikasi/temp').onUpdate(change => {
    const before = change.before.val();
    const after = change.after.val();

    if(before === after){
        console.log('nothing change');
        return null;
    }

    admin.database().ref('/Realtime/rtTemp').once('value', snapshot => {
        var value = snapshot.val();
        admin.database().ref('Device/Token').once('value', childSnapshot => {
            var app_token = childSnapshot.val();
            var message = {
                notification: {
                    title: after,
                    body: 'Temperature Reaches ' + value + '°C'
                },
                 token: app_token
            };
            if(after==='WARM'||after==='HOT'){
                admin.messaging().send(message).then(response => {
                    console.log('Success: ', response);
                    return response;
                }).catch(error => {
                    console.log('error: ',error);
                    return error;
                });
            }else return console.log('TEMP tidak ada');
        });
        return null;
    });
});

exports.water = functions.database.ref('Trigger/bHour').onUpdate(change => {
    const before = change.before.val();
    const after = change.after.val();

    if(before === after){
        console.log('nothing change');
        return null;
    }
    //Hitung Fuzzy
    const refFuzzy = admin.database().ref('Fuzzy/fzSource');
    refFuzzy.once('value', snapshot => {
        var fzSrc = [];
        snapshot.forEach(childSnapshot => {
            //Mengambil variabel perhitungan dari firebase
            fzSrc.push(childSnapshot.val());
        });

        var soil = [];
        var temp = [];

        var valSoil = fzSrc[0];
        var valTemp = fzSrc[1];
        var valWater = fzSrc[2];

        //Member Fuzzy Tanah
        fSegi4(valSoil,60,75,100,100);
        soil.push(memberFuzzy);
        fSegi3(valSoil,25,57.5,75);
        soil.push(memberFuzzy);
        fSegi4(valSoil,0,0,25,55);
        soil.push(memberFuzzy);

        //Member Fuzzy Suhu
        fSegi4(valTemp,0,0,20,25);
        temp.push(memberFuzzy);
        fSegi3(valTemp,20,25,30);
        temp.push(memberFuzzy);
        fSegi3(valTemp,25,30,35);
        temp.push(memberFuzzy);
        fSegi3(valTemp,30,35,40);
        temp.push(memberFuzzy);
        fSegi4(valTemp,35,40,80,80);
        temp.push(memberFuzzy);

        //Implementasi Rule Fuzzy
        var rule = [];
        for(var i=0;i<temp.length;i++){
            for(var j=0;j<soil.length;j++){
                rule.push(Math.min(temp[i],soil[j]));
            }
        }

        //Defuzifikasi
        var defuz = [];
        var limitDefuz = 0;
        var ruleDefuz = 0;
        for(i = 0; i < rule.length ; i++){
            if(limitDefuz<3){
                defuz.push(rule[i]*air[ruleDefuz]); 
            }else {
                ruleDefuz = ruleDefuz - 2;
                defuz.push(rule[i]*air[ruleDefuz]); 
                limitDefuz = 0;
            }
            ruleDefuz++;
            limitDefuz++;
        }

        //Mencari jumlah untuk perhitungan hasil
        var sumDefuz = 0;
        var sumRule = 0;
        for(i = 0; i < defuz.length ; i++){
            sumDefuz = sumDefuz + defuz[i];
            sumRule = sumRule + rule[i];
        }
        var hasil = (sumDefuz/sumRule) * valWater;
        hasil = Math.round((hasil + Number.EPSILON) * 100) / 100;
        return admin.database().ref('Relay/water/fuzzyAuto').set(hasil);
    });

    //Set Siram
    var auto;
    admin.database().ref('Relay/auto').once('value', snapshot => {
        auto = snapshot.val();
        if(auto){
            admin.database().ref('Relay/time/time1').once('value', snapshot => {
                var value = snapshot.val();
                if(value === after) { admin.database().ref('Relay/control').set(3); }
            });
            admin.database().ref('Relay/time/time2').once('value', snapshot => {
                var value = snapshot.val();
                if(value === after) { admin.database().ref('Relay/control').set(3); }
            });
        }
    });
    return null;
});

exports.fuzzyManual = functions.database.ref('Fuzzy/fzSource/fzWater').onUpdate(change => {
    const before = change.before.val();
    const after = change.after.val();

    if(before === after){
        console.log('nothing change');
        return null;
    }

    const refFuzzy = admin.database().ref('Fuzzy/fzSource');
    refFuzzy.once('value', snapshot => {
        var fzSrc = [];
        snapshot.forEach(childSnapshot => {
            //Mengambil variabel perhitungan dari firebase
            fzSrc.push(childSnapshot.val());
        });

        var soil = [];
        var temp = [];

        var valSoil = fzSrc[0];
        var valTemp = fzSrc[1];
        var valWater = fzSrc[2];


        console.log('Nilai: '+valSoil +' '+ valTemp +' ' +valWater);

        //Member Fuzzy Tanah
        fSegi4(valSoil,60,75,100,100);
        soil.push(memberFuzzy);
        fSegi3(valSoil,25,57.5,75);
        soil.push(memberFuzzy);
        fSegi4(valSoil,0,0,25,55);
        soil.push(memberFuzzy);
        
        var strSoil = 'Soil'
        soil.forEach(element =>{ strSoil = strSoil +' '+element;});
        console.log(strSoil);

        //Member Fuzzy Suhu
        fSegi4(valTemp,0,0,20,25);
        temp.push(memberFuzzy);
        fSegi3(valTemp,20,25,30);
        temp.push(memberFuzzy);
        fSegi3(valTemp,25,30,35);
        temp.push(memberFuzzy);
        fSegi3(valTemp,30,35,40);
        temp.push(memberFuzzy);
        fSegi4(valTemp,35,40,80,80);
        temp.push(memberFuzzy);

        var strTemp = 'Temp'
        temp.forEach(element =>{ strTemp = strTemp +' '+element;});
        console.log(strTemp);

        //Implementasi Rule Fuzzy
        var rule = [];
        for(var i=0;i<temp.length;i++){
            for(var j=0;j<soil.length;j++){
                rule.push(Math.min(temp[i],soil[j]));
            }
        }

        //Defuzifikasi
        var defuz = [];
        var limitDefuz = 0;
        var ruleDefuz = 0;
        for(i = 0; i < rule.length ; i++){
            if(limitDefuz<3){
                defuz.push(rule[i]*air[ruleDefuz]); 
            }else {
                ruleDefuz = ruleDefuz - 2;
                defuz.push(rule[i]*air[ruleDefuz]); 
                limitDefuz = 0;
            }
            ruleDefuz++;
            limitDefuz++;
        }

        //Mencari jumlah untuk perhitungan hasil
        var sumDefuz = 0;
        var sumRule = 0;
        for(i = 0; i < defuz.length ; i++){
            sumDefuz = sumDefuz + defuz[i];
            sumRule = sumRule + rule[i];
        }
        var hasil = (sumDefuz/sumRule) * valWater;
        hasil = Math.round((hasil + Number.EPSILON) * 100) / 100;
        admin.database().ref('Relay/water/fuzzyAuto').set(hasil);
    });
    return null;
});