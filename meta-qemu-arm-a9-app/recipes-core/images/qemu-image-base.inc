inherit core-image
inherit image-buildinfo
inherit features_check

COMPATIBLE_MACHINE = "qemuarma9"

IMAGE_FEATURES += "splash package-management ssh-server-dropbear hwcodecs"

REQUIRED_DISTRO_FEATURES = "wayland"

CORE_IMAGE_BASE_INSTALL += "weston weston-init weston-examples gtk+3-demo clutter-1.0-examples"
CORE_IMAGE_BASE_INSTALL += "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'weston-xwayland matchbox-terminal', '', d)}"

IMAGE_INSTALL_append = "\
	bash \
    helloworld \
    python3 \
    python3-pudb \
    python3-urwid \
    python3-urwid-readline \
    htop \
    devmem2 \
"

IMAGE_FSTYPES = "tar.bz2"
