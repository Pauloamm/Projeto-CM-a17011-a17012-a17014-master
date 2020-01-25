package com.example.floristav100

import android.os.Bundle
import android.os.PersistableBundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_history)

        supportActionBar!!.hide()

        historyTextView.movementMethod = ScrollingMovementMethod()

        historyTextView.text = "Nascida em 1926 com óbito em 1996, Dona Lurdes era uma simples senhora da aldeia, com o típico coração alegre e simples!\n\n" +
                "Proveniente de família pobre, esta sobreviveu colhendo e vendendo flores do quintal de seus pais, ficando conhecida na sua terra como a casamenteira dos casais novos com todo o tipo de combinações e especialidades de arranjos.\n\n" +
                "Em idade adulta decidiu então abrir uma florista que rapidamente ganhou fama e sucesso devido ao ambiente acolhedor e simples.\n\n" +
                "Seus filhos então continuaram o legado de sua mãe mantendo esta florista como um marco da sua terra, um marco histórico e que a todos derrete!\n\n" +
                "Assim, foi desenvolvida esta aplicação, não só para manter este negócio em evolução nos tempos modernos mas também para dar a conhecer a tão bela história que até hoje marca os corações de quem conheceu a senhora e sua familia!"
    }
}