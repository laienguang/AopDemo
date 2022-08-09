package com.aop.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aop.cglib.CglibProxyFactory
import com.aop.demo.databinding.ActivityMainBinding
import com.aop.proxy.ProxyFactory
import com.aop.proxy.ProxyInterface

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    class ProxyImpl: ProxyInterface {
        override fun work(input: String): String {
            return "hello $input"
        }
    }

    open class CgLibTest {
        open fun work(input: String): String {
            return "hello $input"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnTest.setOnClickListener {
            val proxyInstance = ProxyFactory.getProxyInstance(ProxyImpl())
            val cglibInstance2 = CglibProxyFactory.getProxyInstance(this, CgLibTest())
            proxyInstance.work("world")
            cglibInstance2.work("world")
            AspectTest().test("world")
            KtAspectTest().test("world")
        }
    }
}