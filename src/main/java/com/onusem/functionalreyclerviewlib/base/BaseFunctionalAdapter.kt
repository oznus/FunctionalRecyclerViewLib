package com.onusem.functionalreyclerviewlib.base

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup


/**
 * Created by onusem on 7/27/17.
 */
class BaseFunctionalAdapter<in T,out VB : ViewDataBinding>(
        private val bindFunc: (viewBindings: VB, item: T,position :Int,payload :List<Any>) -> Unit
        , private val viewBindingFactoryFunc: (inflater : LayoutInflater ) -> VB)

    : RecyclerView.Adapter<BaseFunctionalAdapter.BaseViewHolder>() {

    private val items: MutableList<T> = arrayListOf()

    override fun onBindViewHolder(holder: BaseFunctionalAdapter.BaseViewHolder, position: Int,payloads :List<Any>) {
        bindFunc.invoke(holder.binding as VB, getItem(position),position,payloads)
    }


    override fun onBindViewHolder(holder: BaseFunctionalAdapter.BaseViewHolder, position: Int) {
        bindFunc.invoke(holder.binding as VB, getItem(position),position, emptyList())
    }


    override fun getItemViewType(position: Int): Int = 0

    private fun getItem(position: Int): T = items[position]

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(viewBindingFactoryFunc(LayoutInflater.from(parent.context)))
    }
    

    fun add(items: List<T>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }



    open class BaseViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)



}