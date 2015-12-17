#include "wx/wxprec.h"
#ifdef __BORLANDC__
    #pragma hdrstop
#endif
#ifndef WX_PRECOMP
    #include "wx/wx.h"
#endif

// the application icon (under Windows and OS/2 it is in resources)
#if defined(__WXGTK__) || defined(__WXMOTIF__) || defined(__WXMAC__) || defined(__WXMGL__) || defined(__WXX11__)
    #include "mondrian.xpm"
#endif

class wxTextCtrl;

class GUI_Main : public wxFrame
{
public:
    GUI_Main(const wxPoint& pos, const wxSize& size, long style = wxDEFAULT_FRAME_STYLE);
    ~GUI_Main();

    // event handlers (these functions should _not_ be virtual)
    void OldFiles(wxCommandEvent& event);
    void Resources(wxCommandEvent& event);

private:
    wxTextCtrl* Text;
    DECLARE_EVENT_TABLE() // any class wishing to process wxWindows events must use this macro
};

