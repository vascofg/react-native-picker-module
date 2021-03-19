import React, { useEffect } from "react"
import { NativeModules, View, NativeEventEmitter } from "react-native"

function usePrevious(value) {
  const ref = React.useRef()
  React.useEffect(() => {
    ref.current = value
  })
  return ref.current
}

const ReactNativePickerModule = ({
  value,
  items,
  title,
  selectedColor,
  onValueChange,
  pickerRef,
  onCancel,
  onDelete,
}) => {
  const [isShown, setIsShown] = React.useState(false)
  const prevItems = usePrevious(items)

  useEffect(() => {
    pickerRef.current = {
      show: () => {
        setIsShown(true)
        return NativeModules.ReactNativePickerModule.show(
          items,
          value ? value.toString() : null,
          title,
          selectedColor ? parseColor(selectedColor) : null,
          Boolean(onDelete),
          value => {
            onValueChange(value)
            setIsShown(false)
          },
          () => {
            if (onCancel) onCancel()
            setIsShown(false)
          },
        )
      },
      hide: () => {
        setIsShown(false)
        return NativeModules.ReactNativePickerModule.hide()
      },
    }
  })
  useEffect(() => {
    const eventEmitter = new NativeEventEmitter(NativeModules.ReactNativePickerModule)
    const callback = event => {
      onDelete(event.value)
    }
    eventEmitter.addListener("ItemDeleted", callback)
    return () => {
      eventEmitter.removeListener("ItemDeleted", callback)
    }
  }, [onDelete])
  React.useEffect(() => {
    // re-create dialog if items change
    if (isShown && JSON.stringify(items) !== JSON.stringify(prevItems)) {
      NativeModules.ReactNativePickerModule.hide()
      if (items && items.length > 0) {
        pickerRef.current.show()
      } else {
        // all items deleted, cancel dialog
        pickerRef.current.hide()
      }
    }
  }, [pickerRef, items, isShown, prevItems])

  return <View style={{ display: "none" }} />
}

const parseColor = input => {
  if (input.substr(0, 1) === "#") {
    let collen = (input.length - 1) / 3
    let fact = [17, 1, 0.062272][collen - 1]
    return [
      Math.round(parseInt(input.substr(1, collen), 16) * fact),
      Math.round(parseInt(input.substr(1 + collen, collen), 16) * fact),
      Math.round(parseInt(input.substr(1 + 2 * collen, collen), 16) * fact),
    ]
  } else
    return input
      .split("(")[1]
      .split(")")[0]
      .split(",")
      .map(x => +x)
}
export default ReactNativePickerModule
