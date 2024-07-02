
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SpaceItemDecoration(private val space: Int, private val orientation: Int) :
    ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        // Only add top/bottom margin if the item is not the first one
        val position = parent.getChildAdapterPosition(view)
        if (position != 0) {
            if (orientation == RecyclerView.VERTICAL) {
                outRect.top = space
            } else {
                outRect.left = space
            }
        }
    }
}
