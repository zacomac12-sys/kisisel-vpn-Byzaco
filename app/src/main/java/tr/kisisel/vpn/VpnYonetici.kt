package tr.kisisel.vpn

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config

/** Yapılandırma (özel anahtar dahil) Android Keystore ile şifreli saklanır. */
object Depo {
    private fun tercihler(c: Context): SharedPreferences {
        val anahtar = MasterKey.Builder(c.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        return EncryptedSharedPreferences.create(
            c.applicationContext, "gizli", anahtar,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun oku(c: Context): String? = tercihler(c).getString("conf", null)
    fun yaz(c: Context, metin: String) = tercihler(c).edit().putString("conf", metin).apply()
}

/** WireGuard tünelini açıp kapatır. Ağ işlemi yaptığı için ana iş parçacığında çağırmayın (acikMi hariç). */
object VpnYonetici {
    private var backend: GoBackend? = null

    private val tunel = object : Tunnel {
        override fun getName(): String = "kisisel"
        override fun onStateChange(yeni: Tunnel.State) {}
    }

    private fun b(c: Context): GoBackend =
        backend ?: GoBackend(c.applicationContext).also { backend = it }

    fun acikMi(c: Context): Boolean = b(c).getState(tunel) == Tunnel.State.UP

    fun ac(c: Context) {
        val metin = Depo.oku(c) ?: return
        val cfg = Config.parse(metin.byteInputStream())
        b(c).setState(tunel, Tunnel.State.UP, cfg)
    }

    fun kapat(c: Context) {
        b(c).setState(tunel, Tunnel.State.DOWN, null)
    }
}
