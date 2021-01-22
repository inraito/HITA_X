package com.stupidtree.hita.ui.eas.imp

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.stupidtree.hita.data.model.eas.CourseItem
import com.stupidtree.hita.data.model.eas.TermItem
import com.stupidtree.hita.data.model.timetable.TimePeriodInDay
import com.stupidtree.hita.data.repository.EASRepository
import com.stupidtree.hita.ui.base.DataState
import com.stupidtree.hita.ui.base.Trigger
import com.stupidtree.hita.utils.MTransformations
import java.util.*

class ImportTimetableViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * 仓库区
     */
    private val easRepository = EASRepository.getInstance(application)

    /**
     * LiveData区
     */

    private val termsController = MutableLiveData<Trigger>()

    val termsLiveData: LiveData<DataState<List<TermItem>>> =
        Transformations.switchMap(termsController) {
            return@switchMap easRepository.getAllTerms()
        }


    val selectedTermLiveData: MutableLiveData<TermItem?> = MutableLiveData()

    val startDateLiveData: MediatorLiveData<DataState<Calendar>> =
        MTransformations.switchMap(selectedTermLiveData) {
            it?.let { it1 ->
                return@switchMap easRepository.getStartDateOfTerm(it1)
            }
            val r = MutableLiveData<DataState<Calendar>>()
            r.value = DataState(Calendar.getInstance())
            return@switchMap r
        }

    val importTimetableResultLiveData = MediatorLiveData<DataState<Boolean>>()

    val scheduleStructureLiveData: LiveData<DataState<MutableList<TimePeriodInDay>>> =
        Transformations.switchMap(selectedTermLiveData) {
            return@switchMap it?.let { it1 -> easRepository.getScheduleStructure(it1) }
        }


    /**
     * 方法区
     */
    fun startRefreshTerms() {
        termsController.value = Trigger.actioning
    }

    fun changeSelectedTerm(termItem: TermItem) {
        selectedTermLiveData.value = termItem
    }

    fun startGetAllTerms(): List<TermItem> {
        if (termsLiveData.value != null && termsLiveData.value!!.data != null) {
            return termsLiveData.value!!.data!!
        }
        return listOf()
    }

    fun startImportTimetable(): Boolean {
        selectedTermLiveData.value?.let { term ->
            startDateLiveData.value?.let { date ->
                scheduleStructureLiveData.value?.let { schedule->
                    if (schedule.data != null && date.state == DataState.STATE.SUCCESS && date.data != null) {
                        easRepository.startImportTimetableOfTerm(
                            term,
                            date.data!!,
                            schedule.data!!,
                            importTimetableResultLiveData
                        )
                        return true
                    }
                }

            }

        }
        return false
    }


    fun changeStartDate(date: Calendar) {
        startDateLiveData.value = DataState(date, DataState.STATE.SPECIAL)
    }

}