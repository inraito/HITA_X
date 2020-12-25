package com.stupidtree.hita.ui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.stupidtree.hita.R
import com.stupidtree.hita.databinding.DialogBottomSelectableListBinding
import com.stupidtree.hita.databinding.DialogBottomSelectableListItemBinding
import com.stupidtree.hita.ui.base.BaseViewHolder
import com.stupidtree.hita.ui.base.BasicSelectableListAdapter
import java.util.*

/**
 * 圆角的文本框底部弹窗
 */
class PopUpSelectableList<T> : TransparentBottomSheetDialog<DialogBottomSelectableListBinding>() {

    @StringRes
    var init_title: Int? = null

    @StringRes
    var init_hint: Int? = null
    var init_text: String? = null

    /**
     * 适配器区
     */
    internal lateinit var listAdapter: LAdapter<T>

    /**
     * 不得已放在UI里的数据
     */
    private var listRes: MutableList<ItemData<T>>? = null
    private var init_selected: T? = null
    private var onConfirmListener: OnConfirmListener<T>? = null

    interface OnConfirmListener<T> {
        fun onConfirm(title: String?, key: T)
    }

    fun setTitle(@StringRes title: Int): PopUpSelectableList<T> {
        init_title = title
        return this
    }

    fun setText(text: String?): PopUpSelectableList<T> {
        init_text = text
        return this
    }

    fun setListData(titles: List<String?>, keys: List<T>): PopUpSelectableList<T> {
        listRes = ArrayList()
        for (i in 0 until titles.size.coerceAtMost(keys.size)) {
            (listRes as ArrayList<ItemData<T>>).add(ItemData(titles[i], keys[i]))
        }
        return this
    }

    fun setHint(@StringRes hint: Int): PopUpSelectableList<T> {
        init_hint = hint
        return this
    }

    fun setInitValue(value: T?): PopUpSelectableList<T> {
        init_selected = value
        return this
    }

    fun setOnConfirmListener(onConfirmListener: OnConfirmListener<T>): PopUpSelectableList<T> {
        this.onConfirmListener = onConfirmListener
        return this
    }
    

    override fun onStart() {
        super.onStart()
        if (init_selected != null) {
            listAdapter.setSelected(ItemData(null, init_selected!!))
        }
        listAdapter.notifyDataSetChanged()
    }

    override fun initViews(v: View) {
        listAdapter = LAdapter(requireContext(), listRes!!)
        binding.list.adapter = listAdapter
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        if (init_title != null) {
            binding.title.setText(init_title!!)
        }
        binding.cancel.setOnClickListener { dismiss() }
        binding.confirm.setOnClickListener {
            if (onConfirmListener != null) {
                val data = listAdapter.selectedData
                Log.e("data", data.toString())
                if (data != null) {
                    onConfirmListener!!.onConfirm(data.name, data.data)
                }
            }
            dismiss()
        }
    }

    class ItemData<K>(var name: String?, var data: K) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || javaClass != other.javaClass) return false
            val keyData = other as ItemData<*>
            return data == keyData.data
        }

        override fun hashCode(): Int {
            return Objects.hash(data)
        }

    }

    @SuppressLint("ParcelCreator")
    internal class LAdapter<C>(mContext: Context, mBeans: MutableList<ItemData<C>>) : BasicSelectableListAdapter<ItemData<C>, LAdapter.LHolder>(
        mContext, mBeans) {
        override fun bindHolder(holder: LHolder, data: ItemData<C>?, position: Int) {
            if (data != null) {
                holder.binding.text.text = data.name
                holder.binding.item.setOnClickListener { selectItem(position, data) }
            }
            if (position == selectedIndex) { //若被选中
                holder.binding.selected.visibility = View.VISIBLE
            } else {
                holder.binding.selected.visibility = View.GONE
            }

        }

        internal class LHolder(view:DialogBottomSelectableListItemBinding) : BaseViewHolder<DialogBottomSelectableListItemBinding>(view) {
        }


        override fun createViewHolder(viewBinding: ViewBinding, viewType: Int): LHolder {
            return LHolder(viewBinding as DialogBottomSelectableListItemBinding)
        }

        override fun getViewBinding(parent: ViewGroup, viewType: Int): ViewBinding {
            return DialogBottomSelectableListItemBinding.inflate(mInflater,parent,false)
        }
    }



    override fun getLayoutId(): Int {
        return R.layout.dialog_bottom_selectable_list
    }

    override fun initViewBinding(v: View): DialogBottomSelectableListBinding {
        return DialogBottomSelectableListBinding.bind(v)
    }
}