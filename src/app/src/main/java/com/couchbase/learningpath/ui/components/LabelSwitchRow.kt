package com.couchbase.learningpath.ui.components
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@Composable
fun LabelSwitchRow(text: String,
                   isSwitched: Boolean,
                   onCheckChanged: (Boolean) -> Unit)
{
    Row()
    {
        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp))
        {
            val (label, switch) = createRefs()

            Text(text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(label) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(switch.start)
                        width = Dimension.fillToConstraints
                    })
            Switch(modifier = Modifier.constrainAs(switch) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }, checked = isSwitched,
                onCheckedChange = { onCheckChanged(it)
                })
        }
    }
}