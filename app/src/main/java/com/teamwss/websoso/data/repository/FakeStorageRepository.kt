package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.model.StorageEntity
import javax.inject.Inject

class FakeStorageRepository @Inject constructor() {

    fun getInterestNovels(): List<StorageEntity.UserNovel> {
        return listOf(
            StorageEntity.UserNovel(
                "연진",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                4.5,
                "나는 전생에 뽀로로 왜냐면 노는게 제일 좋아 다음 생에는 뽀로로 태어나야",
                1001
            ),
            StorageEntity.UserNovel(
                "연진",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                4.3,
                "나는이연진이야만나서반가워",
                1001
            ),
            StorageEntity.UserNovel(
                "연진",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                4.5,
                "나는 전생에 뽀로로",
                1001
            ),
            StorageEntity.UserNovel(
                "연진",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                4.5,
                "오늘qa인데나는다못했어홀리몰리어쩌냐진자이연진파이팅해보자연진아할수있다",
                1001
            ),
            StorageEntity.UserNovel(
                "연진",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                0.0,
                "나는 전생에 뽀로로",
                1001
            ),
            StorageEntity.UserNovel(
                "연진",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                4.5,
                "나는 전생에 뽀로로 왜냐면 노는게 제일 좋아 다음 생에는 뽀로로 태어나야",
                1001
            ),
            StorageEntity.UserNovel(
                "연진",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                4.3,
                "나는이연진이야만나서반가워",
                1001
            ),
            StorageEntity.UserNovel(
                "연진",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                4.5,
                "나는 전생에 뽀로로",
                1001
            ),
            StorageEntity.UserNovel(
                "연진",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                4.5,
                "오늘qa인데나는다못했어홀리몰리어쩌냐진자이연진파이팅해보자연진아할수있다",
                1001
            ),
        )
    }

    fun getWatchingNovels(): List<StorageEntity.UserNovel> {
        return listOf(
            StorageEntity.UserNovel(
                "명지",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                4.5,
                "나는 전생에 뽀로로",
                1001
            ),
            StorageEntity.UserNovel(
                "명지",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                0.0,
                "나는 전생에 뽀로로",
                1001
            ),
            StorageEntity.UserNovel(
                "명지",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                4.5,
                "나는 전생에 뽀로로",
                1001
            ),
            StorageEntity.UserNovel(
                "명지",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                2.4,
                "나는 전생에 뽀로로",
                1001
            ),
            StorageEntity.UserNovel(
                "명지",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                4.5,
                "나는 전생에 뽀로로",
                1001
            ),
        )
    }

    fun getWatchedNovels(): List<StorageEntity.UserNovel> {
        return listOf(
            StorageEntity.UserNovel(
                "준서",
                1,
                "https://s3-alpha-sig.figma.com/img/6068/40b2/804b087dd60d29b29fa5b686adce4631?Expires=1727049600&Key-Pair-Id=APKAQ4GOSFWCVNEHN3O4&Signature=mWCVuahpu0t~rn-5z5HuhMVousrbnp~FGh32c-CleVNmpBPPTNtizRS1OpV4MY3Xf18sSS8PIwIolIspIyJsuoL~YQ72AQ2HtiznhxhB~Cse4wXRHcALR2KSsSZRUtdLcdC3goMIlHzvYd4bvyGKZ7krVRkpGEoZ3LRGIO~yW25rNfI6-TURo9qQ~vOzSdcUs2DvB8svU1Xgy5b0MhdIbA4hgIx3J9JbMpsCkR5B3BTZjq3kkxrqfiJIst6vh-vyHU19O-F6cga6Lq1SEzXDex8eMx2FVKJCI5DfYJ9oUiwaMgf6rpbkAfrCl-JE6~CMPm218IjNFTStiqkQdBHAkA__",
                4.5,
                "나는 전생에 뽀로로",
                1001
            ),
        )
    }

    fun getQuittingNovels(): List<StorageEntity.UserNovel> {
        return listOf(
        )
    }
}