package fr.epf.min.movieappepf.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// pour rajouter une marge de 20 px entre chaque item mode vertical
class MovieItemDecoration :RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom=20
    }

}