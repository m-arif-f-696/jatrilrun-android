# Integrasi Laravel (28 November 2026)

Backend: `https://www.jatrilrun.my.id/` (kontrak dibandingkan dengan branch `dev` dari `m-arif-f-696/jatrilrun-project`).

## Terhubung di Android

- `POST /api/auth/signup`: membuat akun; pengguna harus memverifikasi email sebelum masuk. Signup tidak dianggap sebagai login.
- `POST /api/auth/signin` dilanjutkan `GET /api/auth/session`: memakai cookie sesi Sanctum. Pengguna lokal disimpan hanya setelah sesi server terkonfirmasi.
- `POST /api/auth/signout`: menghapus sesi server dan lokal.
- `GET /api/events` dan `GET /api/events/{slug}`: panel "Event dari server" di Beranda menampilkan daftar, detail, keadaan kosong, dan error jaringan.
- `GET/POST/PATCH /api/profiles/me`: pendaftaran server memeriksa profil dan membuat atau memperbaruinya dari Data peserta serta Kontak darurat.
- `POST /api/registrations`: membuat pendaftaran dengan UUID event/kategori dari server setelah persetujuan.
- `GET /api/transactions`, `POST /api/media/upload`, dan `POST /api/transactions/{id}/pay`: menampilkan nominal/status server serta mengunggah foto bukti pembayaran untuk QRIS atau transfer bank.
- `GET /api/registrations` dan `GET /api/registrations/{id}/ticket`: menampilkan status pendaftaran dan token tiket server pada tab Tiket Saya.

Alur event contoh dari Figma tetap tersedia sebagai **simulasi**. Tombol "Lanjutkan simulasi tanpa akun" melewati autentikasi server. Pembayaran demo dan race pass diberi penjelasan bahwa keduanya tidak membuat transaksi nyata. Alur event server menggunakan pembayaran manual sesuai Laravel dan tidak pernah masuk ke layar sukses demo secara otomatis.

## Batasan dan perbedaan desain

- Form server menambahkan nomor identitas, jenis identitas, dan golongan darah karena wajib di backend. Nama BIB, jersey, komunitas, informasi medis, dan persetujuan waiver belum memiliki field pada endpoint pendaftaran; nilainya belum tersimpan di backend.
- Backend menerima `QRIS` atau `TRANSFER_BANK` dengan foto bukti dan persetujuan admin. Alur VA BCA/BNI dalam Figma hanya simulasi; backend tidak mengirim nomor rekening/QR pembayaran, sehingga aplikasi mengarahkan pengguna memakai instruksi resmi penyelenggara sebelum mengunggah bukti.
- Tiket server menampilkan token teks. QR race pass Figma masih aset demo; perlu generator QR dari token server dan pengujian scan sebelum bisa dipakai untuk check-in.
- Draft formulir berada di memori; jika proses aplikasi dimatikan sebelum `POST /api/registrations`, isian dapat hilang. Setelah pendaftaran dibuat, route pembayaran menyimpan ID registrasi.

`GET https://www.jatrilrun.my.id/api/events` mengembalikan HTTP 200 dengan `data: []` pada 28 November 2026. Karena belum ada event yang dipublikasikan, pendaftaran nyata tidak dapat diuji end-to-end tanpa data event/staging. Jangan menggunakan tombol pembayaran demo sebagai bukti transaksi server.

## Verifikasi

`./gradlew :app:testDebugUnitTest :app:assembleDebug` berhasil. Unit test kontrak memeriksa respons akun UUID, daftar event kosong, payload profil/pendaftaran, dan status transaksi. Autentikasi dengan akun terverifikasi dan operasi tulis produksi belum diuji tanpa akun/event uji.
