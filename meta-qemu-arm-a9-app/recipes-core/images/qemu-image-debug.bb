DESCRIPTION = "QEMU ARM A9 Debug Image"
LICENSE = "MIT"

require qemu-image-base.inc

COMPATIBLE_MACHINE = "qemuarma9"

IMAGE_INSTALL_append = "\
    gdbserver \
    gdb \
    strace \
    kernel-vmlinux \
    uftrace \
    lttng-tools \
    lttng-modules \
    ltrace \
    systemtap \
"

EXTRA_IMAGE_FEATURES = "dbg-pkgs debug-tweaks tools-debug tools-sdk  tools-profile"
IMAGE_INSTALL:append = "kernel-vmlinux"
