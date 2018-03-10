package com.onusem.functionalreyclerviewlib.base

import android.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.onusem.functionalreyclerviewlib.databinding.BaseFunctionalFragmentBinding
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by onusem on 7/25/17.
 */
abstract class BaseFunctionalFragment<T : Parcelable?,VB : ViewDataBinding> : Fragment(){

    private lateinit var binding: BaseFunctionalFragmentBinding
    protected var adapter : BaseFunctionalAdapter<T,VB>? = null
    private var items: List<T>? = null
    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    companion object {
        val ITEMS_LIST_EXTRA = "items_list_extra"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        items = arguments?.getParcelableArrayList<T>(ITEMS_LIST_EXTRA)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        fetchItems()

        adapter = createAdapter()

        binding = BaseFunctionalFragmentBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = getLayoutManager()
        binding.recyclerView.addItemDecoration(itemDecoration())
        binding.recyclerView.adapter = adapter

        if (binding.recyclerView.layoutManager is GridLayoutManager) {
            binding.recyclerView.addOnScrollListener(BaseMultiOnScrollListener(binding.recyclerView.layoutManager as GridLayoutManager))
        }

        items?.let { adapter?.add(items!!) }

        return binding.root
    }



    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
    }

    private fun fetchItems() {

        fetchPageItemFetchingObservable(0)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { t: List<T>? -> t?.let{ items = t; adapter?.add(items!!) }  }
    }

   inner class BaseMultiOnScrollListener(layoutManager: GridLayoutManager) : EndlessRecyclerOnScrollListener(layoutManager) {

        override fun onLoadMore(current_page: Int) {

            fetchPageItemFetchingObservable(current_page)
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe { t: List<T>? -> t?.let{ items = t; adapter?.add(items!!) }  }
        }

    }

    open fun fetchPageItemFetchingObservable(current_page: Int): Flowable<List<T>>? = null


    open fun showToolBar(): Boolean = true

    open fun itemDecoration(): RecyclerView.ItemDecoration {
        return object : RecyclerView.ItemDecoration() {
        }
    }

    open fun getLayoutManager() : RecyclerView.LayoutManager = GridLayoutManager(activity, 4)

    private fun createAdapter(): BaseFunctionalAdapter<T, VB> = BaseFunctionalAdapter(getBindingFunc(),createViewBindingFunc())

    abstract fun createViewBindingFunc(): (inflater : LayoutInflater) -> VB

    abstract fun getBindingFunc(): (bindings :VB, item : T, position : Int, payload : List<Any>) -> Unit

    fun add(items : List<T> ) {
        adapter?.add(items)
    }




}