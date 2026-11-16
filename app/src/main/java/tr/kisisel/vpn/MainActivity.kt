package tr.kisisel.vpn

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.VpnService
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.wireguard.config.Config

class MainActivity : Activity() {
    private lateinit var durum: TextView
    private lateinit var dugme: Button
    private lateinit var kutu: EditText

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        // Ekran görüntüsü / ekran kaydında özel anahtarın sızmasını engeller
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        val p = (24 * resources.displayMetrics.density).toInt()
        val kok = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(p, p, p, p)
        }
        durum = TextView(this).apply { textSize = 24f; gravity = Gravity.CENTER; setPadding(0, p, 0, p) }
        dugme = Button(this).apply { textSize = 20f; setOnClickListener { degistir() } }
        kutu = EditText(this).apply {
            minLines = 8
            gravity = Gravity.TOP
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        }
        val kaydetDugme = Button(this).apply {
            text = "Yapılandırmayı Kaydet"
            setOnClickListener { kaydet() }
        }
        kok.addView(durum)
        kok.addView(dugme)
        kok.addView(TextView(this).apply { text = "\nWireGuard yapılandırması"; textSize = 16f })
        kok.addView(kutu)
        kok.addView(kaydetDugme)
        setContentView(ScrollView(this).apply { addView(kok) })
    }

    override fun onResume() {
        super.onResume()
        guncelle()
    }

    private fun guncelle() {
        val acik = VpnYonetici.acikMi(this)
        durum.text = if (acik) "Bağlı" else "Bağlı değil"
        durum.setTextColor(if (acik) Color.parseColor("#2E7D32") else Color.GRAY)
        dugme.text = if (acik) "BAĞLANTIYI KES" else "BAĞLAN"
        kutu.hint = if (Depo.oku(this) != null)
            "Yapılandırma kayıtlı (güvenlik için gösterilmez). Değiştirmek için yenisini yapıştırın."
        else
            "Sunucunuzdan aldığınız .conf içeriğini buraya yapıştırın"
    }

    private fun degistir() {
        if (Depo.oku(this) == null) { mesaj("Önce yapılandırmayı kaydedin."); return }
        if (VpnYonetici.acikMi(this)) { calistir { VpnYonetici.kapat(this) }; return }
        val izin = VpnService.prepare(this)
        if (izin != null) startActivityForResult(izin, 1) else calistir { VpnYonetici.ac(this) }
    }

    @Deprecated("Basit tutmak için")
    override fun onActivityResult(istek: Int, sonuc: Int, veri: Intent?) {
        super.onActivityResult(istek, sonuc, veri)
        if (istek == 1 && sonuc == RESULT_OK) calistir { VpnYonetici.ac(this) }
        else if (istek == 1) mesaj("VPN izni verilmedi.")
    }

    private fun kaydet() {
        val metin = kutu.text.toString().trim()
        try {
            Config.parse(metin.byteInputStream())
        } catch (e: Exception) {
            mesaj("Yapılandırma geçersiz. Lütfen kontrol edin.")
            return
        }
        Depo.yaz(this, metin)
        kutu.setText("")
        guncelle()
        mesaj("Kaydedildi.")
    }

    private fun calistir(gorev: () -> Unit) {
        Thread {
            try { gorev() } catch (e: Exception) { runOnUiThread { mesaj("İşlem başarısız oldu.") } }
            runOnUiThread { guncelle() }
        }.start()
    }

    private fun mesaj(s: String) = Toast.makeText(this, s, Toast.LENGTH_LONG).show()
}
