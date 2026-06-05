package com.example.otocheck

import com.example.otocheck.Models.Randevu
import com.example.otocheck.Models.RaporIcinVeriModel
import android.util.Log
import com.example.otocheck.Models.AracRaporOzetModel
import com.example.otocheck.Models.EkspertizAramaModel
import com.example.otocheck.Models.GecmisRaporModel
import com.example.otocheck.Models.PaketModel
import com.example.otocheck.Models.SirketRandevuModel
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.security.MessageDigest

object DatabaseBaglantı {
    // 10.0.2.2 localhost bağlanma adresi
    // 1433varsayılan port
    private const val dbUrl = "jdbc:jtds:sqlserver://10.0.2.2:1433/OtoCheckDB"
    private const val dbUser = "otocheck_user"
    private const val dbPass = "123456"

    fun getConnection(): Connection? {
        val policy = android.os.StrictMode.ThreadPolicy.Builder().permitAll().build()
        android.os.StrictMode.setThreadPolicy(policy)

        return try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
            DriverManager.getConnection(dbUrl, dbUser, dbPass)
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        }
    }


    fun kullaniciGiris(email: String, sifre: String, isKurumsal: Boolean): Int {
        var girisID = -1
        val connection = getConnection() ?: return -1

        try {
            val hashliSifre = hashSifre(sifre)
            val idSutunu = if (isKurumsal) "SirketID" else "MusteriID"
            val tablo = if (isKurumsal) "Sirket" else "Musteri"

            val sorgu = "SELECT $idSutunu FROM $tablo WHERE Email = ? AND Sifre = ?"
            val statement = connection.prepareStatement(sorgu)
            statement.setString(1, email)
            statement.setString(2, hashliSifre)

            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                girisID = resultSet.getInt(idSutunu)
            }

            resultSet.close()
            statement.close()
            connection.close()
        } catch (e: Exception) {
            Log.e("OtoCheckTest", "Giriş Hatası: ${e.message}")
        }

        return girisID
    }

    fun hashSifre(sifre: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(sifre.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }


    fun musteriKayit(adSoyad: String, telefon: String, email: String, sifre: String): Boolean {
        val connection = getConnection() ?: return false
        return try {
            val hashliSifre = hashSifre(sifre)
            val sorgu = "{call sp_MusteriKayit(?, ?, ?, ?)}"
            val statement = connection.prepareCall(sorgu)
            statement.setString(1, adSoyad)
            statement.setString(2, telefon)
            statement.setString(3, email)
            statement.setString(4, hashliSifre)


            statement.execute()
            statement.close()
            connection.close()
            true
        } catch (e: Exception) {
            android.util.Log.e("OtoCheckTest", "Müşteri Kayıt Hatası: ${e.message}")
            false
        }
    }

    fun sirketKayit(sirketAdi: String, sehir: String, ilce: String, adres: String, vkn: String, tse: String, telefon: String, email: String, sifre: String, logoPath: String): Boolean {
        val connection = getConnection() ?: return false
        return try {
            val hashliSifre = hashSifre(sifre)
            val sorgu = "{call sp_SirketKayit(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"
            val statement = connection.prepareCall(sorgu)

            statement.setString(1, sirketAdi)
            statement.setString(2, sehir)
            statement.setString(3, ilce)
            statement.setString(4, adres)
            statement.setString(5, vkn)
            statement.setString(6, tse)
            statement.setString(7, telefon)
            statement.setString(8, email)
            statement.setString(9, hashliSifre)
            statement.setString(10, logoPath)

            statement.execute()
            statement.close()
            connection.close()
            true
        } catch (e: Exception) {
            android.util.Log.e("OtoCheckTest", "Şirket Kayıt Hatası: ${e.message}")
            false
        }
    }


    fun randevulariGetir(musteriID: Int): List<Randevu> {
        val liste = mutableListOf<Randevu>()
        val connection = getConnection() ?: return liste

        try {

            val sorgu = "SELECT SirketAdi, Tarih, Saat, Adres FROM vw_MusteriRandevulari WHERE MusteriID = ? ORDER BY Tarih ASC, Saat ASC"
            val statement = connection.prepareStatement(sorgu)
            statement.setInt(1, musteriID)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {

                val sqlTime = resultSet.getTime("Saat")?.toString() ?: ""
                val formatliSaat = if(sqlTime.length >= 5) sqlTime.substring(0, 5) else sqlTime

                liste.add(Randevu(
                    resultSet.getString("SirketAdi"),
                    resultSet.getString("Tarih"),
                    formatliSaat, // TEMİZLENMİŞ SAATİ BURAYA VERİYORUZ
                    resultSet.getString("Adres")
                ))
            }
            connection.close()
        } catch (e: Exception) {
            Log.e("OtoCheckTest", "com.example.otocheck.Models.Randevu Hatası: ${e.message}")
        }
        return liste
    }

    fun kullaniciIsmiGetir(id: Int, isKurumsal: Boolean): String {
        val connection = getConnection() ?: return "Misafir"
        var isim = ""
        try {
            val tablo = if (isKurumsal) "Sirket" else "Musteri"
            val sutun = if (isKurumsal) "SirketAdi" else "AdSoyad"
            val idSutunu = if (isKurumsal) "SirketID" else "MusteriID"

            val sorgu = "SELECT $sutun FROM $tablo WHERE $idSutunu = ?"
            val statement = connection.prepareStatement(sorgu)
            statement.setInt(1, id)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                isim = resultSet.getString(sutun)
            }
            connection.close()
        } catch (e: Exception) { isim = "Hata" }
        return isim
    }

    data class Sehir(val id: Int, val ad: String) {
        override fun toString(): String = ad
    }

    data class Ilce(val id: Int, val ad: String) {
        override fun toString(): String = ad
    }

    fun sehirleriGetir(): List<Sehir> {
        val liste = mutableListOf<Sehir>()
        liste.add(Sehir(0, "Şehir Seçiniz..."))
        val connection = getConnection() ?: return liste
        try {
            val sorgu = "SELECT SehirID, SehirAdi FROM Sehirler ORDER BY SehirAdi"
            val statement = connection.createStatement()
            val rs = statement.executeQuery(sorgu)
            while (rs.next()) {
                liste.add(Sehir(rs.getInt("SehirID"), rs.getString("SehirAdi")))
            }
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return liste
    }

    fun ilceleriGetir(sehirID: Int): List<Ilce> {
        val liste = mutableListOf<Ilce>()
        liste.add(Ilce(0, "İlçe Seçiniz..."))
        if (sehirID == 0) return liste

        val connection = getConnection() ?: return liste
        try {
            val sorgu = "SELECT IlceID, IlceAdi FROM Ilceler WHERE SehirID = ? ORDER BY IlceAdi"
            val statement = connection.prepareStatement(sorgu)
            statement.setInt(1, sehirID)
            val rs = statement.executeQuery()
            while (rs.next()) {
                liste.add(Ilce(rs.getInt("IlceID"), rs.getString("IlceAdi")))
            }
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return liste
    }

    // şirketin o günkü dolu saatlerinin bilgisini alan fonksiyon
    fun getDoluSaatler(sirketId: Int, tarih: String): List<String> {
        val doluSaatler = mutableListOf<String>()
        val connection = getConnection() ?: return doluSaatler
        Log.d("OtoCheckTest", "Sorgulanan Tarih: $tarih, Dolu Saat Sayısı: ${doluSaatler.size}")

        return try {

            val query = "SELECT RandevuSaati FROM com.example.otocheck.Models.Randevu WHERE SirketID = ? AND RandevuTarihi = ?"
            val stmt = connection.prepareStatement(query)
            stmt.setInt(1, sirketId)
            stmt.setString(2, tarih)

            val rs = stmt.executeQuery()

            while (rs.next()) {
                val saatTam = rs.getString("RandevuSaati")

                // SQL den gelen format 10:00:00 şeklinde
                // bunu 10:00 yapmak için ilk 5 karakterini alıyoruz
                if (!saatTam.isNullOrEmpty() && saatTam.length >= 5) {
                    doluSaatler.add(saatTam.substring(0, 5))
                }
            }
            connection.close()
            doluSaatler
        } catch (e: Exception) {
            e.printStackTrace()
            doluSaatler
        }
    }


    fun ekspertizFiltrele(
        sehir: String?, ilce: String?, maxFiyat: Double?,
        motor: Boolean, mekanik: Boolean, kaporta: Boolean, airbag: Boolean, obd: Boolean,
        siralama: String
    ): List<EkspertizAramaModel> {
        val liste = mutableListOf<EkspertizAramaModel>()
        val connection = getConnection() ?: return liste
        try {
            val sorgu = "{call sp_EkspertizFiltrele(?, ?, ?, ?, ?, ?, ?, ?, ?)}"
            val stmt = connection.prepareCall(sorgu)

            stmt.setString(1, if (sehir == "Şehir Seçiniz..." || sehir.isNullOrEmpty()) null else sehir)
            stmt.setString(2, if (ilce == "İlçe Seçiniz..." || ilce.isNullOrEmpty()) null else ilce)
            if (maxFiyat != null && maxFiyat > 0) stmt.setDouble(3, maxFiyat) else stmt.setNull(3, java.sql.Types.DECIMAL)

            stmt.setInt(4, if (motor) 1 else 0)
            stmt.setInt(5, if (mekanik) 1 else 0)
            stmt.setInt(6, if (kaporta) 1 else 0)
            stmt.setInt(7, if (airbag) 1 else 0)
            stmt.setInt(8, if (obd) 1 else 0)
            stmt.setString(9, if (siralama == "Fiyat (Artan)") "FiyatArtan" else "FiyatAzalan")

            val rs = stmt.executeQuery()
            while (rs.next()) {
                val sId = rs.getInt("SirketID")
                val pId = rs.getInt("PaketID")
                val sAdi = try { rs.getString("SirketAdi") } catch (e: Exception) { "OtoCheck Şirketi" }
                val pAdi = rs.getString("PaketAdi")
                val logoStr = try { rs.getString("LogoPath") } catch (e: Exception) { null } // Kendi kolon adına göre "Logo" yazan yeri düzelt
                val fiyat = rs.getDouble("Fiyat")
                // YOK TRY-CATCH, YOK KONUM. DİREKT SQL'DEKİ İSİMLERİYLE ÇEKİYORUZ:
                val sehir = rs.getString("Sehir")
                val ilce = rs.getString("Ilce")
                val konumStr = "$sehir / $ilce"

                val icerikList = mutableListOf<String>()
                if (rs.getBoolean("Icerik_Motor")) icerikList.add("Motor")
                if (rs.getBoolean("Icerik_Mekanik")) icerikList.add("Mekanik")
                if (rs.getBoolean("Icerik_Kaporta")) icerikList.add("Kaporta")
                if (rs.getBoolean("Icerik_Airbag")) icerikList.add("Airbag")
                if (rs.getBoolean("Icerik_OBD")) icerikList.add("OBD")
                val icerikOzet = icerikList.joinToString(", ") + " Testleri"

                // TAM OTURAN CONSTRUCTOR
                val model = EkspertizAramaModel(
                    sirketId = sId,
                    paketId = pId,
                    sirketAdi = sAdi,
                    paketAdi = pAdi,
                    konum = konumStr,
                    fiyat = fiyat,
                    icerikOzet = icerikOzet,
                    logoBase64 = logoStr // LOGOYU MODELE VERDİK
                )
                liste.add(model)
            }
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return liste
    }
    fun randevuOlustur(
        musteriId: Int, sirketId: Int, paketId: Int,
        tarih: String, saat: String, fiyat: Double,
        plaka: String, marka: String, model: String, yil: Int
    ): Boolean {
        val connection = getConnection() ?: return false
        return try {
            val sorgu = "{call sp_RandevuOlustur(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"
            val stmt = connection.prepareCall(sorgu)

            stmt.setInt(1, musteriId)
            stmt.setInt(2, sirketId)
            stmt.setInt(3, paketId)
            stmt.setString(4, tarih)
            stmt.setString(5, saat)
            stmt.setDouble(6, fiyat)


            stmt.setString(7, plaka.trim().uppercase())
            stmt.setString(8, marka.trim())
            stmt.setString(9, model.trim())
            stmt.setInt(10, yil)

            val sonuc = stmt.executeUpdate()
            connection.close()
            sonuc > 0
        } catch (e: Exception) {
            Log.e("OtoCheckHata", "com.example.otocheck.Models.Randevu Oluşturma SQL Hatası: ${e.message}")
            e.printStackTrace()
            false
        }
    }


    fun paketGuncelle(paketId: Int, ad: String, fiyat: Double, motor: Boolean, mekanik: Boolean, kaporta: Boolean, airbag: Boolean, obd: Boolean): Boolean {
        val connection = getConnection() ?: return false
        return try {
            val sorgu = "{call sp_PaketGuncelle(?, ?, ?, ?, ?, ?, ?, ?)}"
            val statement = connection.prepareCall(sorgu)
            statement.setInt(1, paketId)
            statement.setString(2, ad)
            statement.setDouble(3, fiyat)
            statement.setBoolean(4, motor)
            statement.setBoolean(5, mekanik)
            statement.setBoolean(6, kaporta)
            statement.setBoolean(7, airbag)
            statement.setBoolean(8, obd)

            val sonuc = statement.executeUpdate()
            connection.close()
            sonuc > 0
        } catch (e: Exception) {
            Log.e("OtoCheckHata", "Paket Güncelleme Hatası: ${e.message}")
            false
        }
    }

    fun getSirketBugununRandevulari(sirketId: Int, tarih: String): List<SirketRandevuModel> {
        val liste = mutableListOf<SirketRandevuModel>()
        val connection = getConnection() ?: return liste

        try {
            val sorgu = "SELECT * FROM vw_SirketRandevulari WHERE SirketID = ? AND RandevuTarihi = ? ORDER BY RandevuSaati ASC"
            val statement = connection.prepareStatement(sorgu)
            statement.setInt(1, sirketId)
            statement.setString(2, tarih)

            val rs = statement.executeQuery()
            while (rs.next()) {
                val sqlTime = rs.getTime("RandevuSaati").toString()
                val formatliSaat = sqlTime.substring(0, 5)

                val randevu = SirketRandevuModel(
                    randevuId = rs.getInt("RandevuID"),
                    sirketId = rs.getInt("SirketID"),
                    randevuTarihi = rs.getString("RandevuTarihi"),
                    randevuSaati = formatliSaat,
                    paketAdi = rs.getString("PaketAdi"),
                    plaka = rs.getString("Plaka"),
                    marka = rs.getString("Marka"),
                    model = rs.getString("Model"),
                    yil = rs.getInt("Yil"),
                    durum = rs.getString("Durum")
                )
                liste.add(randevu)
            }
            connection.close()
        } catch (e: Exception) {
            Log.e("OtoCheckTest", "Şirket com.example.otocheck.Models.Randevu Çekme Hatası: ${e.message}")
        }
        return liste
    }

    fun getSirketGelecekRandevulari(sirketId: Int): List<SirketRandevuModel> {
        val liste = mutableListOf<SirketRandevuModel>()
        val connection = getConnection() ?: return liste
        try {
            val sorgu = "SELECT * FROM vw_SirketRandevulari WHERE SirketID = ? AND RandevuTarihi >= CAST(GETDATE() AS DATE) ORDER BY RandevuTarihi ASC, RandevuSaati ASC"
            val statement = connection.prepareStatement(sorgu)
            statement.setInt(1, sirketId)

            val rs = statement.executeQuery()
            while (rs.next()) {
                val sqlTime = rs.getTime("RandevuSaati").toString()
                val formatliSaat = sqlTime.substring(0, 5)

                liste.add(
                    SirketRandevuModel(
                        randevuId = rs.getInt("RandevuID"),
                        sirketId = rs.getInt("SirketID"),
                        randevuTarihi = rs.getString("RandevuTarihi"),
                        randevuSaati = formatliSaat,
                        paketAdi = rs.getString("PaketAdi"),
                        plaka = rs.getString("Plaka"),
                        marka = rs.getString("Marka"),
                        model = rs.getString("Model"),
                        yil = rs.getInt("Yil"),
                        durum = rs.getString("Durum")
                    )
                )
            }
            connection.close()
        } catch (e: Exception) { e.printStackTrace() }
        return liste
    }


    fun sirketPaketleriniGetir(sirketId: Int): List<PaketModel> {
        val liste = mutableListOf<PaketModel>()
        val connection = getConnection() ?: return liste
        try {
            val sorgu = "SELECT * FROM vw_SirketPaketleri WHERE SirketID = ? ORDER BY Fiyat ASC"
            val statement = connection.prepareStatement(sorgu)
            statement.setInt(1, sirketId)
            val rs = statement.executeQuery()

            while (rs.next()) {
                liste.add(
                    PaketModel(
                        paketId = rs.getInt("PaketID"),
                        sirketId = rs.getInt("SirketID"),
                        paketAdi = rs.getString("PaketAdi"),
                        fiyat = rs.getDouble("Fiyat"),
                        motor = rs.getBoolean("Icerik_Motor"),
                        mekanik = rs.getBoolean("Icerik_Mekanik"), // YENİ EKLENDİ
                        kaporta = rs.getBoolean("Icerik_Kaporta"),
                        airbag = rs.getBoolean("Icerik_Airbag"),
                        obd = rs.getBoolean("Icerik_OBD")
                    )
                )
            }
            connection.close()
        } catch (e: Exception) { e.printStackTrace() }
        return liste
    }


    fun paketEkle(sirketId: Int, ad: String, fiyat: Double, motor: Boolean, mekanik: Boolean, kaporta: Boolean, airbag: Boolean, obd: Boolean): Boolean {
        val connection = getConnection() ?: return false
        return try {
            // Mekanik eklendiği için soru işareti sayısı 8'e çıktı
            val sorgu = "{call sp_PaketEkle(?, ?, ?, ?, ?, ?, ?, ?)}"
            val statement = connection.prepareCall(sorgu)
            statement.setInt(1, sirketId)
            statement.setString(2, ad)
            statement.setDouble(3, fiyat)
            statement.setBoolean(4, motor)
            statement.setBoolean(5, mekanik) // YENİ EKLENDİ
            statement.setBoolean(6, kaporta)
            statement.setBoolean(7, airbag)
            statement.setBoolean(8, obd)

            val sonuc = statement.executeUpdate()
            connection.close()
            sonuc > 0
        } catch (e: Exception) {
            Log.e("OtoCheckHata", "Paket Ekleme Hatası: ${e.message}")
            false
        }
    }


    fun paketSil(paketId: Int): Boolean {
        val connection = getConnection() ?: return false
        return try {
            val sorgu = "{call sp_PaketSil(?)}"
            val statement = connection.prepareCall(sorgu)
            statement.setInt(1, paketId)

            val sonuc = statement.executeUpdate()
            connection.close()
            sonuc > 0
        } catch (e: Exception) {
            // HATAYI BURADA YAKALAYIP EKRANA YAZDIRIYORUZ!
            Log.e("OtoCheckHata", "Paket Ekleme SQL Hatası: ${e.message}")
            e.printStackTrace()
            false
        }
    }


    fun getRaporIcinVeri(randevuId: Int): RaporIcinVeriModel? {
        val connection = getConnection() ?: return null
        return try {
            val sorgu = "{call sp_RaporIcinVeriGetir(?)}"
            val stmt = connection.prepareCall(sorgu)
            stmt.setInt(1, randevuId)
            val rs = stmt.executeQuery()

            if (rs.next()) {
                RaporIcinVeriModel(
                    randevuId = rs.getInt("RandevuID"),
                    plaka = rs.getString("Plaka"),
                    marka = rs.getString("Marka"),
                    model = rs.getString("Model"),
                    yil = rs.getInt("Yil"),
                    musteriAdi = rs.getString("MusteriAdi"),
                    paketAdi = rs.getString("PaketAdi"),
                    motorVar = rs.getBoolean("Icerik_Motor"),
                    mekanikVar = rs.getBoolean("Icerik_Mekanik"),
                    kaportaVar = rs.getBoolean("Icerik_Kaporta"),
                    airbagVar = rs.getBoolean("Icerik_Airbag"),
                    obdVar = rs.getBoolean("Icerik_OBD")
                )
            } else {
                null
            }
        } catch (e: Exception) {

            android.util.Log.e("OtoCheckTest", "SQL VERİ ÇEKME HATASI: ${e.message}")
            e.printStackTrace()
            null
        }
    }


    fun raporAnaKaydet(randevuId: Int, plaka: String, sase: String, km: Int, marka: String, model: String, yil: Int, yakit: String, vites: String): Int {
        val connection = getConnection() ?: return -1
        return try {
            val sorgu = "{call sp_RaporAnaKaydet(?, ?, ?, ?, ?, ?, ?, ?, ?)}" // 9 Parametre oldu!
            val stmt = connection.prepareCall(sorgu)
            stmt.setInt(1, randevuId)
            stmt.setString(2, plaka)
            stmt.setString(3, sase)
            stmt.setInt(4, km)
            stmt.setString(5, marka)
            stmt.setString(6, model)
            stmt.setInt(7, yil)
            stmt.setString(8, yakit)
            stmt.setString(9, vites)

            val rs = stmt.executeQuery()
            if (rs.next()) rs.getInt("RaporID") else -1
        } catch (e: Exception) {
            android.util.Log.e("OtoCheckTest", "Ana Rapor Kayıt Hatası: ${e.message}")
            -1
        }
    }

    fun raporKaportaKaydet(
        raporId: Int, tavan: String, onTampon: String, arkaTampon: String, kaput: String, bagaj: String,
        solOnCamurluk: String, sagOnCamurluk: String, solArkaCamurluk: String, sagArkaCamurluk: String,
        solOnKapi: String, sagOnKapi: String, solArkaKapi: String, sagArkaKapi: String, solMarspiyel: String, sagMarspiyel: String,
        solOnDirek: String, sagOnDirek: String, solOrtaDirek: String, sagOrtaDirek: String, solArkaDirek: String, sagArkaDirek: String,
        solOnSase: String, sagOnSase: String, solArkaSase: String, sagArkaSase: String, solPodye: String, sagPodye: String, bagajHavuzu: String, aciklama: String
    ): Boolean {
        val connection = getConnection() ?: return false
        return try {
            val sorgu = "{call sp_RaporKaportaKaydet(" + "?,".repeat(29) + "?)}" // Tam 30 Parametre
            val stmt = connection.prepareCall(sorgu)
            stmt.setInt(1, raporId)
            stmt.setString(2, tavan); stmt.setString(3, onTampon); stmt.setString(4, arkaTampon); stmt.setString(5, kaput); stmt.setString(6, bagaj)
            stmt.setString(7, solOnCamurluk); stmt.setString(8, sagOnCamurluk); stmt.setString(9, solArkaCamurluk); stmt.setString(10, sagArkaCamurluk)
            stmt.setString(11, solOnKapi); stmt.setString(12, sagOnKapi); stmt.setString(13, solArkaKapi); stmt.setString(14, sagArkaKapi)
            stmt.setString(15, solMarspiyel); stmt.setString(16, sagMarspiyel)
            stmt.setString(17, solOnDirek); stmt.setString(18, sagOnDirek); stmt.setString(19, solOrtaDirek); stmt.setString(20, sagOrtaDirek); stmt.setString(21, solArkaDirek); stmt.setString(22, sagArkaDirek)
            stmt.setString(23, solOnSase); stmt.setString(24, sagOnSase); stmt.setString(25, solArkaSase); stmt.setString(26, sagArkaSase)
            stmt.setString(27, solPodye); stmt.setString(28, sagPodye); stmt.setString(29, bagajHavuzu); stmt.setString(30, aciklama)
            stmt.executeUpdate() > 0
        } catch (e: Exception) { e.printStackTrace(); false }
    }


    fun raporMotorKaydet(
        raporId: Int, motorSesi: String, ufleme: String, sivi: String, kacak: String,
        turbo: String, sogutma: String, kayis: String, sanziman: String, aciklama: String
    ): Boolean {
        val connection = getConnection() ?: return false
        return try {
            val sorgu = "{call sp_RaporMotorKaydet(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}" // 10 parametre
            val stmt = connection.prepareCall(sorgu)
            stmt.setInt(1, raporId)
            stmt.setString(2, motorSesi); stmt.setString(3, ufleme); stmt.setString(4, sivi); stmt.setString(5, kacak)
            stmt.setString(6, turbo); stmt.setString(7, sogutma); stmt.setString(8, kayis); stmt.setString(9, sanziman); stmt.setString(10, aciklama)
            stmt.executeUpdate() > 0
        } catch (e: Exception) { e.printStackTrace(); false }
    }


    fun raporMekanikKaydet(
        raporId: Int, solAks: String, sagAks: String, direksiyon: String, rot: String,
        onBalata: String, arkaBalata: String, onDisk: String, arkaDisk: String,
        solOnSus: String, sagOnSus: String, solArkaSus: String, sagArkaSus: String, aciklama: String
    ): Boolean {
        val connection = getConnection() ?: return false
        return try {
            val sorgu = "{call sp_RaporMekanikKaydet(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}" // 14 parametre
            val stmt = connection.prepareCall(sorgu)
            stmt.setInt(1, raporId)
            stmt.setString(2, solAks); stmt.setString(3, sagAks); stmt.setString(4, direksiyon); stmt.setString(5, rot)
            stmt.setString(6, onBalata); stmt.setString(7, arkaBalata); stmt.setString(8, onDisk); stmt.setString(9, arkaDisk)
            stmt.setString(10, solOnSus); stmt.setString(11, sagOnSus); stmt.setString(12, solArkaSus); stmt.setString(13, sagArkaSus)
            stmt.setString(14, aciklama)
            stmt.executeUpdate() > 0
        } catch (e: Exception) { e.printStackTrace(); false }
    }


    fun raporObdKaydet(
        raporId: Int, gostergeKm: Int, beyinKm: Int, kmOrijinallik: String,
        tramerDurumu: String, tramerTutari: Double, ecu: String, tcm: String,
        abs: String, airbag: String, elektronik: String, aciklama: String
    ): Boolean {
        val connection = getConnection() ?: return false
        return try {
            val sorgu = "{call sp_RaporObdKaydet(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}" // 12 parametre
            val stmt = connection.prepareCall(sorgu)
            stmt.setInt(1, raporId)
            stmt.setInt(2, gostergeKm); stmt.setInt(3, beyinKm); stmt.setString(4, kmOrijinallik)
            stmt.setString(5, tramerDurumu); stmt.setDouble(6, tramerTutari)
            stmt.setString(7, ecu); stmt.setString(8, tcm); stmt.setString(9, abs)
            stmt.setString(10, airbag); stmt.setString(11, elektronik); stmt.setString(12, aciklama)
            stmt.executeUpdate() > 0
        } catch (e: Exception) { e.printStackTrace(); false }
    }


    fun raporAirbagKaydet(
        raporId: Int, surucu: String, yolcu: String, surucuDiz: String, yolcuDiz: String,
        solPerde: String, sagPerde: String, surucuKoltuk: String, yolcuKoltuk: String,
        kemerler: String, aciklama: String
    ): Boolean {
        val connection = getConnection() ?: return false
        return try {
            val sorgu = "{call sp_RaporAirbagKaydet(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}" // 11 parametre
            val stmt = connection.prepareCall(sorgu)
            stmt.setInt(1, raporId)
            stmt.setString(2, surucu); stmt.setString(3, yolcu)
            stmt.setString(4, surucuDiz); stmt.setString(5, yolcuDiz)
            stmt.setString(6, solPerde); stmt.setString(7, sagPerde)
            stmt.setString(8, surucuKoltuk); stmt.setString(9, yolcuKoltuk)
            stmt.setString(10, kemerler); stmt.setString(11, aciklama)
            stmt.executeUpdate() > 0
        } catch (e: Exception) { e.printStackTrace(); false }
    }
    fun gecmisRaporlariGetir(musteriId: Int): List<GecmisRaporModel> {
        val liste = mutableListOf<GecmisRaporModel>()
        val connection = getConnection() ?: return liste
        return try {
            val sorgu = "SELECT RandevuID, RaporID, SirketAdi, PaketAdi, Tarih, Fiyat, Plaka, Marka, Model FROM vw_GecmisRaporlar WHERE MusteriID = ? ORDER BY Tarih DESC"
            val stmt = connection.prepareStatement(sorgu)
            stmt.setInt(1, musteriId)
            val rs = stmt.executeQuery()

            while (rs.next()) {
                liste.add(
                    GecmisRaporModel(
                        rs.getInt("RandevuID"),
                        rs.getInt("RaporID"),
                        rs.getString("SirketAdi"),
                        rs.getString("PaketAdi"),
                        rs.getString("Tarih"),
                        rs.getDouble("Fiyat"),
                        rs.getString("Plaka"),
                        rs.getString("Marka"),
                        rs.getString("Model")
                    )
                )
            }
            connection.close()
            liste
        } catch (e: Exception) {
            android.util.Log.e("OtoCheckTest", "Geçmiş Raporlar SQL Hatası: ${e.message}")
            liste
        }
    }
    fun raporBulPlakaVeyaSase(aramaMetni: String): Int {
        val connection = getConnection() ?: return -1
        return try {
            // girilen metin plaka veya şaseno sütunlarından herhangi biriyle eşleşirse o raporun ID sini getirir
            val sorgu = "SELECT RaporID FROM Rapor_Ana WHERE Plaka = ? OR SaseNo = ?"
            val stmt = connection.prepareStatement(sorgu)
            stmt.setString(1, aramaMetni)
            stmt.setString(2, aramaMetni)

            val rs = stmt.executeQuery()
            if (rs.next()) rs.getInt("RaporID") else -1

        } catch (e: Exception) {
            -1
        }
    }

    fun plakaVeyaSaseyeGoreRaporlariGetir(aramaMetni: String): List<AracRaporOzetModel> {
        val liste = mutableListOf<AracRaporOzetModel>()
        val connection = getConnection() ?: return liste
        return try {
            val sorgu = "SELECT RaporID, Plaka, SaseNo, Marka, Model, Kilometre, RaporTarihi, SirketAdi, PaketAdi FROM vw_AraçRaporGecmisi WHERE Plaka = ? OR SaseNo = ? ORDER BY RaporID DESC"
            val stmt = connection.prepareStatement(sorgu)
            stmt.setString(1, aramaMetni)
            stmt.setString(2, aramaMetni)
            val rs = stmt.executeQuery()

            while (rs.next()) {
                liste.add(
                    AracRaporOzetModel(
                        rs.getInt("RaporID"),
                        rs.getString("Plaka"),
                        rs.getString("SaseNo"),
                        rs.getString("Marka"),
                        rs.getString("Model"),
                        rs.getInt("Kilometre"),
                        rs.getString("RaporTarihi"),
                        rs.getString("SirketAdi"),
                        rs.getString("PaketAdi")
                    )
                )
            }
            connection.close()
            liste
        } catch (e: Exception) {
            liste
        }
    }
    fun raporDetaylariniGetir(raporId: Int): Map<String, String> {
        val sonuc = mutableMapOf<String, String>()
        val connection = getConnection() ?: return sonuc

        try {

            val sorgu = "SELECT * FROM vw_RaporTumDetaylar WHERE RaporID = ?"
            val stmt = connection.prepareStatement(sorgu)
            stmt.setInt(1, raporId)
            val rs = stmt.executeQuery()

            if (rs.next()) {
                val metaData = rs.metaData
                val kolonSayisi = metaData.columnCount


                for (i in 1..kolonSayisi) {
                    val kolonAdi = metaData.getColumnName(i)
                    sonuc[kolonAdi] = rs.getString(kolonAdi) ?: "Test Edilmedi"
                }
            }
            connection.close()
        } catch (e: Exception) {
            android.util.Log.e("OtoCheckTest", "Detay Çekme Hatası: ${e.message}")
        }
        return sonuc
    }
    fun sirketIstatistikGetir(sirketId: Int): Map<String, String> {
        val sonuc = mutableMapOf<String, String>()
        val connection = getConnection() ?: return sonuc

        try {

            val sorgu = "EXEC sp_SirketIstatistikGetir ?"
            val stmt = connection.prepareStatement(sorgu)
            stmt.setInt(1, sirketId)
            val rs = stmt.executeQuery()

            if (rs.next()) {
                sonuc["BugunRandevu"] = rs.getString("BugunRandevu") ?: "0"
                sonuc["YarinRandevu"] = rs.getString("YarinRandevu") ?: "0"
                sonuc["GunlukKazanc"] = rs.getString("GunlukKazanc") ?: "0.00"
                sonuc["EnCokSatanPaket"] = rs.getString("EnCokSatanPaket") ?: "Henüz Satış Yok"
            }
            connection.close()
        } catch (e: Exception) {
            android.util.Log.e("OtoCheckTest", "İstatistik Çekme Hatası: ${e.message}")
        }
        return sonuc
    }
    fun kullaniciProfilBilgileriniGetir(kullaniciId: Int, isKurumsal: Boolean): Map<String, String> {
        val profilBilgileri = mutableMapOf<String, String>()
        val connection = getConnection() ?: return profilBilgileri

        try {
            val sorgu = "SELECT Isim, Telefon, Email FROM vw_ProfilBilgileri WHERE KullaniciID = ? AND IsKurumsal = ?"
            val stmt = connection.prepareStatement(sorgu)

            stmt.setInt(1, kullaniciId)
            // Kotlin'deki true/false değerini SQL'deki 1/0 mantığına çeviriyoruz
            stmt.setInt(2, if (isKurumsal) 1 else 0)

            val rs = stmt.executeQuery()

            if (rs.next()) {
                profilBilgileri["Isim"] = rs.getString("Isim") ?: "Bilinmiyor"
                profilBilgileri["Telefon"] = rs.getString("Telefon") ?: "Belirtilmemiş"
                profilBilgileri["Email"] = rs.getString("Email") ?: "Belirtilmemiş"
            }
            connection.close()
        } catch (e: Exception) {
            android.util.Log.e("OtoCheck", "Profil Bilgileri Çekme Hatası: ${e.message}")
        }
        return profilBilgileri
    }
}