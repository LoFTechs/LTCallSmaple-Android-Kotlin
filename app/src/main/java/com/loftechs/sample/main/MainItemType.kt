package com.loftechs.sample.main

import androidx.fragment.app.Fragment
import com.loftechs.sample.call.list.CallListFragment

enum class MainItemType {
    CALL {
        override fun getFragment(): Fragment {
            return CallListFragment.newInstance()
        }

        override fun onFabClick(view: MainContract.View?) {
            view?.gotoCreateCall()
        }
    };

    abstract fun getFragment(): Fragment
    abstract fun onFabClick(view: MainContract.View?)
}