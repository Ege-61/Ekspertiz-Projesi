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
<img width="480" height="1002" alt="Ekran görüntüsü 2026-06-05 164814" src="https://github.com/user-attachments/assets/a4eda085-3ad5-45b9-b6b4-0cc67cbd9047" />
<img width="472" height="999" alt="Ekran görüntüsü 2026-06-05 165130" src="https://github.com/user-attachments/assets/ca3d3805-0190-47d3-aa7b-49ab987c0cb7" />
<img width="473" height="998" alt="Ekran görüntüsü 2026-06-05 165106" src="https://github.com/user-attachments/assets/98ac6e8d-125a-4838-8973-f58a5bcc90f4" />
<img width="479" height="1004" alt="Ekran görüntüsü 2026-06-05 164959" src="https://github.com/user-attachments/assets/43fd41c2-6e47-4593-b011-1eb73f90d482" />
<img width="474" height="997" alt="Ekran görüntüsü 2026-06-05 164921" src="https://github.com/user-attachments/assets/15880241-14fd-430d-9672-069f55047f18" />
<img width="469" height="1006" alt="Ekran görüntüsü 2026-06-05 164855" src="https://github.com/user-attachments/assets/b332cad8-97bc-40b2-8cbb-48b6a0e2a8ae" />
<img width="473" height="996" alt="Ekran görüntüsü 2026-06-05 164840" src="https://github.com/user-attachments/assets/48584044-1a3c-4b45-ae60-4ad2e827757f" />


## Yazılım Mimarisi
* Geliştirme Ortamı: Android Studio
* Yazılım Dil: Kotlin
* Veritabanı Yönetim Sistemi: Microsoft SQL Server (MSSQL)


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
```
## Genel Yapı
OtoCheck sistemi, sunucu (MSSQL Veritabanı) ve istemci (Android Mobil Uygulama) olmak üzere iki ana katmandan oluşan kapsamlı bir mimariye sahiptir.

* **Veritabanı Katmanı:** Sistem, 5N normalizasyon standartlarına tam uyumlu 13 adet ilişkisel tablodan oluşmaktadır. Kurumsal (Şirket) ve Bireysel (Müşteri) olarak iki ayrı kullanıcı tipini yönetir. Şehir/İlçe bazlı arama, dinamik paket filtreleme ve hiyerarşik ekspertiz raporu (Motor, Mekanik, Kaporta, Airbag, OBD vb.) kayıtları birbirine entegredir. Randevu statülerini ve çakışmaları kontrol eden otomatik tetikleyiciler (Trigger), şirketlerin istatistiklerini hesaplayan saklı yordamlar (Stored Procedure) ve veri bütünlüğünü sağlayan kısıtlamalar (Constraints) ile sistem tutarlı bir şekilde çalışır. Test süreçleri için tüm tablolara anlamlı
 test verileri (dummy data) girilmiştir.
* **İstemci (Mobil) Katmanı:** Android tarafında kullanıcı deneyimini artırmak için Navigation Component ve ViewBinding teknolojileri kullanılmıştır. Kullanıcılar sisteme giriş yaptıktan sonra araçlarını kaydedebilir, istedikleri lokasyondaki ekspertiz firmalarını ve paketlerini listeleyebilir, çakışma kontrollü randevu oluşturabilir ve geçmiş raporlarını dijital olarak detaylıca görüntüleyebilirler.

## Referanslar
1. Kocaeli Üniversitesi Bilişim Sistemleri Mühendisliği TBL331 Veritabanı Yönetim Sistemleri Ders Dokümanları
2. BTK Akademi & Atıl Samancıoğlu - "Kotlin ile İleri Seviye Android Mobil Uygulama Geliştirme" Eğitimleri
4. Android Developer Documentation (Photo Picker API, ViewBinding, Navigation Component)
5. Google Gemini AI (Veritabanı çakışma algoritmaları, Photo Picker çözümleri ve mantıksal tasarım optimizasyonu)
6. Mermaid.js Diagram Documentation
