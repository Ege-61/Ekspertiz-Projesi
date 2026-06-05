## Problem Tanımı
Günümüzde ikinci el araç alım satımında ve ekspertiz süreçlerinde çeşitli sıkıntılar yaşanmaktadır. Her ekspertiz firmasının kendine has bir randevu sistemi olması, kullanıcıların fiyat ve paket karşılaştırması yapmasını oldukça zorlaştırmaktadır.
Ek olarak, rapor sonuçlarının genellikle basılı bir kağıt olarak verilmesi; raporun kaybolması, deforme olması veya firmaların tasarımları taklit edilerek sahte (yalan) ekspertiz raporları üretilmesi gibi güvenlik sorunlarına yol açmaktadır.

Geliştirdiğimiz "OtoCheck" uygulaması ile bu sorunların önüne geçmeyi hedefledik. Sistemimiz, randevu alırken paket kapsamlarına göre daha hesaplı ve uygun olanı tercih edebilmenizi sağlamaktadır.
Ayrıca araç satın almak isteyen kullanıcılar, plaka veya şase numarası sorgusu ile geçmişteki ekspertiz raporlarını dijital ortamda güvenle inceleyebilmektedir. "Bu sorunlara nasıl bir çözüm üretebiliriz?" sorusundan yola çıkarak,
sahip olduğumuz Android Studio ve Kotlin bilgi birikimiyle bu mobil uygulamayı hayata geçirdik. Gerçek hayattaki ekspertiz sistemlerinde kontrol edilen çok fazla nokta ve devasa bir veri akışı bulunmaktadır. Projenin kapsamını yönetilebilir tutmak ve 
performans sorunlarını engellemek adına, uygulamamızda rapor verilerini belli başlı 5 ana kategori (Motor, Mekanik, Kaporta, Airbag, OBD) bazında modeledik.

## Yapılan Araştırmalar
* Uygulama geliştirme sürecinde bizi algoritmik olarak en çok zorlayan kısım "Randevu Alma" işlemi oldu. Aynı şirket, aynı tarih ve saat için mükerrer randevu alınması ihtimalini ortadan kaldırmamız gerekti.
Bu çakışma kontrolünün mantığını kurarken ve veritabanı ile haberleştirirken yapay zekadan destek aldık.
* Rapor yükleme aşaması teknik bir zorluktan ziyade, ekspertiz sürecindeki veri giriş noktalarının çokluğundan dolayı oldukça fazla vakit alan ve uğraştıran bir süreç oldu.
* Projede arayüz elemanlarını bağlamak ve ekranlar arası geçişi sağlamak için ViewBinding ve Navigation Component sistemlerini kullandık.
 Bu yapıların güncel kurulum ve tanımlama kodlarını doğrudan resmi dokümantasyon olan https://developer.android.com/ adresinden araştırarak entegre ettik.
* Şirket logosu yüklemek için galeriye erişim izni istememiz gerekiyordu. Uygulamamız geniş bir Android sürüm yelpazesini (Android 7 ve üstü) desteklediği için ve Android 11 öncesi/sonrası depolama izin sistemleri değiştiği için karmaşık izin kodları yazmamız gerekecekti.
Ancak yaptığımız araştırmalar (ve yapay zeka desteği) ile bunun çok daha modern ve kolay bir yolu olduğunu öğrendik: **Android Photo Picker "ActivityResultContracts.PickVisualMedia()" yapısı.
Bu yapı sayesinde kullanıcılardan tehlikeli depolama izinleri (READ_EXTERNAL_STORAGE) istemeden, sistemin kendi güvenli arayüzü üzerinden galeriden sadece istenen fotoğrafı seçtirmeyi başardık.


## Akış Şeması
*(Sistemin nasıl çalıştığını gösteren uygulamanın ekran görüntülerini buraya sürükleyip bırakabilirsiniz)


## Yazılım Mimarisi
* Geliştirme Ortamı: Android Studio
* İstemci (Client) Dili: Kotlin / Java (Android)
* Veritabanı Yönetim Sistemi: Microsoft SQL Server (MSSQL)
* Bağlantı Mimarisi: jTDS JDBC Driver
* Geliştirme Aşamaları:  İş mantığını veritabanı düzeyinde çözmek amacıyla Stored Procedure, güvenli veri çekimi için View, otomatik durum güncellemeleri için Trigger ve performans için Index yapıları aşama aşama kurgulanmış ve Android arayüzü ile bağlanmıştır.

## Veri Tabanı Diyagramı (Genişletilmiş Kavramsal Model)
Aşağıdaki diyagram, sistemin temel varlıkları arasındaki ilişkilerini ve Birincil Anahtarlarını ER diyagramı şeklinde göstermektedir:

```mermaid
graph TD
    %% Nitelikler (Elips - Yuvarlak, Altı Çizili PK'lar)
    m1(["<u>MusteriID</u>"])
    m2([AdSoyad])
    
    a1(["<u>ArabaID</u>"])
    a2([Plaka])
    
    s1(["<u>SirketID</u>"])
    s2([SirketAdi])
    s3([VKN])

    p1(["<u>PaketID</u>"])
    p2([PaketAdi])
    p3([Fiyat])
    
    r1(["<u>RandevuID</u>"])
    r2([Tarih])
    
    rap1(["<u>RaporID</u>"])
    rap2([Kilometre])

    mot1(["<u>MotorID</u>"])
    mot2([MotorSesi])

    %% Varlıklar (Dikdörtgen)
    Musteri[MÜŞTERİ]
    Araba[ARABA]
    Sirket[ŞİRKET]
    Paket[PAKET]
    Randevu[RANDEVU]
    Rapor[RAPOR_ANA]
    RaporMotor[RAPOR_MOTOR]

    %% İlişkiler (Baklava)
    Rel1{Sahiptir}
    Rel2{Alır}
    Rel3{Kabul Eder}
    Rel4{Sunar}
    Rel5{İçerir}
    Rel6{Dönüşür}
    Rel7{Alt Detayıdır}

    %% Nitelikleri Varlıklara Bağlama
    Musteri --- m1 & m2
    Araba --- a1 & a2
    Sirket --- s1 & s2 & s3
    Paket --- p1 & p2 & p3
    Randevu --- r1 & r2
    Rapor --- rap1 & rap2
    RaporMotor --- mot1 & mot2

    %% Varlıkları İlişkilere Bağlama (KARDİNALİTE: 1'e N ve 1'e 1 Gösterimi)
    Musteri ---|1| Rel1 ---|N| Araba
    Musteri ---|1| Rel2 ---|N| Randevu
    
    Sirket ---|1| Rel3 ---|N| Randevu
    Sirket ---|1| Rel4 ---|N| Paket
    
    Paket ---|1| Rel5 ---|N| Randevu
    
    Randevu ---|1| Rel6 ---|1| Rapor
    Rapor ---|1| Rel7 ---|1| RaporMotor
