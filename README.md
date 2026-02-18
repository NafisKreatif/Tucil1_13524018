# IF2211 Stategi Algortima Tucil 1 2026

## Author

Nama: Muhammad Nafis Habibi

NIM: 13524018

## Deskripsi Tugas

Queens adalah gim logika yang tersedia pada situs jejaring profesional LinkedIn. Tujuan dari gim ini adalah menempatkan queen pada sebuah papan persegi berwarna sehingga terdapat tepat hanya satu queen pada tiap baris, kolom, dan daerah warna. Selain itu, satu queen tidak dapat ditempatkan bersebelahan dengan queen lainnya, termasuk secara diagonal.

Penyelesaian harus dilakukan dengan algoritma brute force. Berdasarkan spesifikasi tugas yang diberikan, program harus dapat:

1.	Menemukan satu solusi penempatan queen pada suatu papan berwarna yang diberikan, atau menampilkan bahwa tidak ada solusi yang valid.
2.	Melakukan validasi input yang diberikan.
3.	Menampilkan banyak konfigurasi atau iterasi yang ditinjau oleh algoritma
4.	Menampilkan waktu eksekusi program dalam millisecond (selain waktu input/output).
5.	Memvisualisasikan proses brute force yang dilakukan (Live Update)

Ada beberapa batasan masukan yang perlu divalidasi:

1.	Masukan berupa matriks karakter (array dua dimensi) berukuran n x n. Artinya besar baris dan kolom sama.
2.	Masukan berupa karakter dari ‘A’ sampai ‘Z’.
3.	Besar papan memiliki besar maksimal yaitu 26.
4.	Jumlah daerah warna yang ada sama dengan besar papan.
5.	Tidak ada daerah warna yang terpisah dari daerah warna yang sama. Daerah warna terhubung secara ortogonal dan tidak diagonal. (Batasan ini sepertinya tidak wajib divalidasi karena sulit untuk di-brute force)


## Folder Structure

```
├───bin
├───docs
├───src
│   └───main
│       ├───java
│       │   └───stima
│       └───resources
│           ├───css
│           ├───fxml
│           └───images
├───target
├───test
├───pom.xml
├───Makefile
└───README.md
```

## Requirements

Sebelum menjalankan program, pastikan Anda sudah menginstall hal-hal berikut:

### Java
- **Version:** 17 or higher
- **Download links:**
  - [Oracle JDK 17](https://www.oracle.com/java/technologies/downloads)

### Maven
- **Version:** 3.2.5 or higher (recommended 3.6.3+)
- **Download links:**
  - [Direct Apache Maven Official Downloads](https://dlcdn.apache.org/maven/maven-3/3.9.11/binaries/apache-maven-3.9.11-bin.zip)

## Instalasi Maven

### Windows
Untuk instalasi maven, unduh dokumen .zip dan dokumen tersebut akan berisi struktur sebagai berikut
```
apache-maven-<version>/
├── bin/               <-- skrip yang diekskusi (mvn, mvn.cmd)
├── boot/         
├── conf/          
├── lib/          
├── NOTICE
├── LICENSE
├── README.txt
```

Letakkan bin/ pada environment PATH untuk dapat digunakan pada terminal. [Add folder to PATH tutorial](https://www.youtube.com/watch?v=pGRw1bgb1gU)

### Linux
```bash
sudo apt update
sudo apt install openjdk-17-jdk -y
sudo apt install maven -y
```

## Menjalankan Program

Cukup jalankan Makefile dengan `make` atau `make run`

```bash
make run
```