package com.stupidtree.hita.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.stupidtree.hita.R
import com.stupidtree.hita.databinding.ActivityMainBinding
import com.stupidtree.hita.ui.base.BaseActivity
import com.stupidtree.hita.ui.base.BaseTabAdapter
import com.stupidtree.hita.ui.main.timeline.FragmentTimeLine
import com.stupidtree.hita.utils.ActivityUtils
import com.stupidtree.hita.utils.ImageUtils

/**
 * 很显然，这是主界面
 */
@SuppressLint("NonConstantResourceId")
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    /**
     * 抽屉里的View
     */
    private var drawerAvatar: ImageView? = null
    private var drawerNickname: TextView? = null
    private var drawerUsername: TextView? = null
    private var drawerHeader: ViewGroup? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setWindowParams(statusBar = true, darkColor = true, navi = false)
    }


    override fun onResume() {
        super.onResume()
        //setUserViews(viewModel.localUser)
    }

    private fun setUpDrawer() {
        binding.drawerNavigationview.itemIconTintList = null
        val headerView = binding.drawerNavigationview.inflateHeaderView(R.layout.activity_main_nav_header)
        binding.drawer.setStatusBarBackgroundColor(Color.TRANSPARENT)
        binding.drawer.setScrimColor(getBackgroundColorSecondAsTint())
        binding.drawer.drawerElevation = ImageUtils.dp2px(this, 84f).toFloat()
        drawerAvatar = headerView.findViewById(R.id.avatar)
        drawerHeader = headerView.findViewById(R.id.drawer_header)
        drawerNickname = headerView.findViewById(R.id.nickname)
        drawerUsername = headerView.findViewById(R.id.username)
        binding.drawer.addDrawerListener(object : DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                //offset 偏移值
                val mContent = binding.drawer.getChildAt(0)
                val scale = 1 - slideOffset
                val rightScale = 0.8f + scale * 0.2f
                mContent.translationX = -drawerView.measuredWidth * slideOffset
                //mContent.setAlpha(0.3f+0.7f*scale);
                mContent.pivotX = mContent.measuredWidth.toFloat()
                mContent.pivotY = (mContent.measuredHeight shr 1.toFloat().toInt()).toFloat()
                mContent.invalidate()
                mContent.scaleX = rightScale
                mContent.scaleY = rightScale
            }

            override fun onDrawerOpened(drawerView: View) {
               // setUserViews(viewModel.localUser)
            }

            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })

        binding.drawerNavigationview.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.drawer_nav_my_profile -> {
                    ActivityUtils.startLoginEASActivity(getThis())
                    true
                }
                else -> false
            }
        }
    }

    override fun initViews() {

        setUpDrawer()
        binding.title.text = binding.navView.menu.getItem(0).title
        //Objects.requireNonNull(getSupportActionBar()).setTitle(navView.getMenu().getItem(0).getTitle());
        binding.pager.adapter = object : BaseTabAdapter(supportFragmentManager, 1) {
            override fun initItem(position: Int): Fragment {
                return FragmentTimeLine()
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                super.destroyItem(container, position, `object`)
            }
        }
        binding.pager.offscreenPageLimit = 3
        binding.pager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                val item = binding.navView.menu.getItem(position)
                item.isChecked = true
                binding.title.text = item.title
                //Objects.requireNonNull(getSupportActionBar()).setTitle(item.getTitle());
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        binding.navView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.navigation_timeline-> binding.pager.currentItem = 0
               //R.id.navigation_dashboard -> binding.pager.currentItem = 1
            }
            binding.title.text = item.title
            true
        }
        binding.avatar.setOnClickListener { binding.drawer.openDrawer(GravityCompat.END) }
    }

//    private fun setUserViews(userLocalInfo: UserLocal) {
//        if (userLocalInfo.isValid) { //如果已登录
//            //装载头像
//            ImageUtils.loadLocalAvatarInto(this, userLocalInfo.avatar, drawerAvatar!!)
//            ImageUtils.loadLocalAvatarInto(this, userLocalInfo.avatar, binding.avatar)
//            //设置各种文字
//            drawerUsername!!.text = userLocalInfo.username
//            drawerNickname!!.text = userLocalInfo.nickname
//            drawerHeader!!.setOnClickListener { ActivityUtils.startProfileActivity(getThis(), viewModel.localUser.id!!) }
//            binding.drawerNavigationview.setNavigationItemSelectedListener { item: MenuItem ->
//                when (item.itemId) {
//                    R.id.drawer_nav_my_profile -> {
//                        ActivityUtils.startProfileActivity(getThis(), viewModel.localUser.id!!)
//                        true
//                    }
//                    R.id.drawer_nav_scan_qr -> {
//                        ActivityUtils.startQRCodeActivity(getThis())
//                        true
//                    }
//                    R.id.drawer_nav_discover_friend -> {
//                        ActivityUtils.startSearchActivity(getThis())
//                        true
//                    }
//                    else -> false
//                }
//            }
//        } else {
//            //未登录的信息显示
//            drawerUsername!!.setText(R.string.not_logged_in)
//            drawerNickname!!.setText(R.string.please_log_in)
//            drawerAvatar!!.setImageResource(R.drawable.place_holder_avatar)
//            binding.avatar.setImageResource(R.drawable.place_holder_avatar)
//            drawerHeader!!.setOnClickListener { ActivityUtils.startLoginActivity(getThis()) }
//            binding.drawerNavigationview.setNavigationItemSelectedListener { item: MenuItem ->
//                if (item.itemId == R.id.drawer_nav_my_profile) {
//                    ActivityUtils.startLoginActivity(getThis())
//                    return@setNavigationItemSelectedListener true
//                }
//                false
//            }
//        }
//    }

    override fun onBackPressed() {
        //super.onBackPressed();
        if (binding.drawer.isDrawerOpen(GravityCompat.END)) {
            binding.drawer.closeDrawer(GravityCompat.END)
            return
        }
        //返回桌面而非退出
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }


    override fun getViewModelClass(): Class<MainViewModel> {
        return MainViewModel::class.java
    }

    override fun initViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
}