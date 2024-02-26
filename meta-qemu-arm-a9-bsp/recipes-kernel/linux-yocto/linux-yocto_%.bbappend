KBRANCH:qemuarma9 ?= "v6.1/standard/arm-versatile-926ejs"
KMACHINE:qemuarma9 ?= "qemuarma9"
COMPATIBLE_MACHINE:qemuarma9 = "qemuarma9"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

#SRC_URI += "file://0001-update-for-vexpress-test.patch"

SRC_URI:append = " file://bsp.cfg"
KERNEL_FEATURES:append = " bsp.cfg"
