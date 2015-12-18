/*  Copyright (c) MediaArea.net SARL. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license that can
 *  be found in the License.html file in the root of the source tree.
 */

//---------------------------------------------------------------------------
// Pre-compilation
#include "MediaInfo/PreComp.h"
#ifdef __BORLANDC__
    #pragma hdrstop
#endif
//---------------------------------------------------------------------------

//---------------------------------------------------------------------------
#include "MediaInfo/Setup.h"
//---------------------------------------------------------------------------

//---------------------------------------------------------------------------
#if defined(MEDIAINFO_IT_YES)
//---------------------------------------------------------------------------

//---------------------------------------------------------------------------
#include "MediaInfo/Audio/File_ImpulseTracker.h"
//---------------------------------------------------------------------------

namespace MediaInfoLib
{

//***************************************************************************
// Static stuff
//***************************************************************************

//---------------------------------------------------------------------------
bool File_ImpulseTracker::FileHeader_Begin()
{
    //Element_Size
    if (Buffer_Size<4)
        return false; //Must wait for more data

    if (CC4(Buffer)!=0x494D504D) //"IMPM"
    {
        Reject("Impulse Tracker");
        return false;
    }

    //All should be OK...
    return true;
}

//***************************************************************************
// Buffer - Global
//***************************************************************************

//---------------------------------------------------------------------------
void File_ImpulseTracker::Read_Buffer_Continue()
{
    //Parsing
    Ztring SongName;
    int16u OrdNum, InsNum, SmpNum, PatNum, Flags, Special;
    int8u  VersionMajor, VersionMinor, SoftwareVersionMajor, SoftwareVersionMinor, IS, TS;
    bool Stereo;
    Skip_B4(                                                    "Signature");

    Get_Local(26, SongName,                                     "Song name");
    Skip_L1(                                                    "Unknown");
    Skip_L1(                                                    "Unknown");
    Get_L2 (OrdNum,                                             "Orders count");
    Get_L2 (InsNum,                                             "Instruments count");
    Get_L2 (SmpNum,                                             "Samples count");
    Get_L2 (PatNum,                                             "Paterns count");
    Get_L1 (SoftwareVersionMinor,                               "Cwt/v (Minor)");
    Get_L1 (SoftwareVersionMajor,                               "Cwt/v (Major)");
    Get_L1 (VersionMinor,                                       "Cwt (Minor)");
    Get_L1 (VersionMajor,                                       "Cwt (Major)");
    Get_L2 (Flags,                                              "Flags");
        Get_Flags (Flags, 0, Stereo,                            "Stereo");
        Skip_Flags(Flags, 1,                                    "Vol0MixOptimizations");
        Skip_Flags(Flags, 2,                                    "Use instruments/Samples");
        Skip_Flags(Flags, 3,                                    "Linear/Amiga slides");
        Skip_Flags(Flags, 4,                                    "Old/IT Effects");
    Get_L2 (Special,                                            "Special");
        Skip_Flags(Special, 0,                                  "Song Message attached");
    Skip_L1(                                                    "Global volume");
    Skip_L1(                                                    "Mix volume");
    Get_L1 (IS,                                                 "Initial Speed");
    Get_L1 (TS,                                                 "Initial Temp");
    Skip_L1(                                                    "Panning separation between channels");
    Skip_L1(                                                    "0");
    Skip_L2(                                                    "Message Length");
    Skip_L4(                                                    "Message Offset");
    Skip_L1(                                                    "Unknown");
    Skip_L1(                                                    "Unknown");
    Skip_L1(                                                    "Unknown");
    Skip_L1(                                                    "Unknown");
    Skip_L1(                                                    "Unknown");
    Skip_XX(64,                                                 "Chnl Pan");
    Skip_XX(64,                                                 "Chnl Vol");
    Skip_XX(OrdNum,                                             "Orders");
    Skip_XX(InsNum*4,                                           "Instruments");
    Skip_XX(SmpNum*4,                                           "Samples");
    Skip_XX(PatNum*4,                                           "Patterns");

    FILLING_BEGIN();
        Accept("Impulse Tracker");

        Fill(Stream_General, 0, General_Format, "Impulse Tracker");
        Fill(Stream_General, 0, General_Format_Version, Ztring(__T("Version "))+Ztring::ToZtring(VersionMajor)+__T(".")+Ztring::ToZtring(VersionMinor/16)+Ztring::ToZtring(VersionMinor%16));
        Fill(Stream_General, 0, General_Track, SongName);
        Fill(Stream_General, 0, General_Encoded_Application, Ztring(__T("Impulse Tracker ")+Ztring::ToZtring(SoftwareVersionMajor)+__T(".")+Ztring::ToZtring(SoftwareVersionMinor/16)+Ztring::ToZtring(SoftwareVersionMinor%16)));
        Fill(Stream_General, 0, "BPM", TS);

        Stream_Prepare(Stream_Audio);
        Fill(Stream_Audio, StreamPos_Last, Audio_Channel_s_, Stereo?2:1);

        //No more need data
        Finish("Impulse Tracker");
    FILLING_END();
}

//***************************************************************************
// C++
//***************************************************************************

} //NameSpace

#endif //MEDIAINFO_IT_YES
