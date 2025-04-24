package org.catinthedark.alyoep.lib.states

import com.badlogic.gdx.Gdx
import org.catinthedark.alyoep.lib.IOC
import org.catinthedark.alyoep.lib.atOr

class StateMachine {
    private val states: MutableMap<String, IState> = hashMapOf()
    private val mixins: MutableMap<String, MutableList<() -> Unit>> = hashMapOf()
    private var currentState: String = ""

    fun putMixin(key: String, mixin: () -> Unit) {
        mixins.getOrPut(key, { mutableListOf() }).add(mixin)
    }

    fun putMixins(vararg keys: String, mixin: () -> Unit) {
        keys.forEach { putMixin(it, mixin) }
    }

    fun put(key: String, state: IState) {
        states[key] = state
    }

    fun putAll(vararg pairs: Pair<String, IState>) {
        pairs.forEach { put(it.first, it.second) }
    }

    fun onUpdate() {
        val state = IOC.atOr("state", "")
        if (state != currentState) {
            Gdx.app.log(this::class.simpleName, "Transition from $currentState to $state")
            states[currentState]?.onExit()
            states[state]?.onActivate()
            currentState = state
        }

        states[currentState]?.onUpdate() ?: Gdx.app.log(this::class.simpleName, "Unknown '$state'")
        mixins[currentState]?.forEach { it() }
    }
}
